package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource(name = "redissonClient1")
    private RedissonClient redissonClient;
    @Resource
    @Lazy
    private IVoucherOrderService proxy;
    private static DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private static final ExecutorService SECKILL_ORDER_EXECUTOR= Executors.
            newSingleThreadExecutor();

    @PostConstruct//在当前类初始化完毕后来执行
    private void init(){
        //延迟2秒，等Redis连接池完全就绪
        SECKILL_ORDER_EXECUTOR.submit(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            new VoucherOrderHandler().run();
        });
    }
    private class VoucherOrderHandler implements Runnable{
        String queueName="stream.orders";
        @Override
        public void run(){
            while(true){

                try {
                    //1.获取消息队列中的订单信息XREADGROUP GROUP g1 c1 COUNT 1BLOCK 2000 STREAMS streams.order >
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed()));
                    //2.判断消息获取是否成功
                    if(list==null||list.isEmpty()){
                        //2.1获取失败 说明没有消息 继续下次循环
                        continue;
                    }
                    //2.2获取成功 可以下单
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    handleVoucherOrder(voucherOrder);
                    //3、ACK确认 SACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName,"g1",record.getId());

                } catch (Exception e) {
                   //出现异常 没有ack确认  会进入pending list
                    log.error("处理订单异常",e);
                        handlePendingList();

                }

            }
        }

        private void handlePendingList() {
            while(true){

                try {
                    //1.获取pending-list中的订单信息XREADGROUP GROUP g1 c1 COUNT 1 2000 STREAMS streams.order 0
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                );
                    //2.判断消息获取是否成功
                    if(list==null||list.isEmpty()){
                        //2.1获取失败 说明pending没有异常消息 结束循环
                        break;
                    }
                    //2.2获取成功 可以下单
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    handleVoucherOrder(voucherOrder);
                    //3、ACK确认 SACK stream.orders g1 id
                    stringRedisTemplate.opsForStream().acknowledge(queueName,"g1",record.getId());

                } catch (Exception e) {
                    //出现异常 没有ack确认  会进入pending list
                    log.error("处理pending-list订单异常",e);

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

            }

        }
    }
//    private BlockingQueue<VoucherOrder> orderTasks=new
//            ArrayBlockingQueue<>(1024*1024);
//    private class VoucherOrderHandler implements Runnable{
//        @Override
//        public void run(){
//            while(true){
//
//                try {
//                    //1.获取队列中的订单信息
//                    VoucherOrder voucherOrder = orderTasks.take();
//                    //2.创建订单
//                    handleVoucherOrder(voucherOrder);
//                } catch (Exception e) {
//                    log.error("处理订单异常",e);
//                }
//
//            }
//        }
//    }

    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        //1.获取用户
        Long userId = voucherOrder.getUserId();
        //2.创建锁对象
        //SimpleRedisLock lock = new SimpleRedisLock("order"+userId,stringRedisTemplate);
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        //3.获取锁
        boolean isLock = lock.tryLock();
        if(!isLock) {
            //获取锁失败
            log.error("不允许重复下单");
            return;
        }
        try {

             proxy.createVoucherOrder(voucherOrder);//这个函数其实是用this调用的 给省略了
        }finally{
            lock.unlock();
        }
    }

    @Override

    public Result seckilVoucher(Long voucherId) {
        //获取用户
        Long userId = UserHolder.getUser().getId();
        //获取订单id
        long orderId = redisIdWorker.nextId("order");
        //1.执行lua脚本
        Long result = stringRedisTemplate.execute(SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(),
                userId.toString(),
                String.valueOf(orderId));
        //2.判断结果是否为0
        int r=result.intValue();
        if(r!=0){
            //2.1不为0 没有购买资格 返回错误信息
            return Result.fail(r==1?"库存不足":"不能重复下单");
        }

        //获取代理对象
         proxy = (IVoucherOrderService) AopContext.currentProxy();
        //3.返回订单id
        return Result.ok(orderId);

   }
//    @Override
//
//    public Result seckilVoucher(Long voucherId) {
//        //1.查询优惠卷
//        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
//        //2.判断秒杀是否开始
//        if(voucher.getBeginTime().isAfter(LocalDateTime.now())){
//            //尚未开始
//            return Result.fail("秒杀尚未开始");
//        }
//        if(voucher.getEndTime().isBefore(LocalDateTime.now())){
//            //尚未结束
//            return Result.fail("秒杀已经结束");
//        }
//
//         //4.判断库存是否充足
//        if(voucher.getStock()<1){
//            //库存不足
//            return Result.fail("库存不足！");
//        }
//        Long userId = UserHolder.getUser().getId();
//
//        //创建锁对象
//        //SimpleRedisLock lock = new SimpleRedisLock("order"+userId,stringRedisTemplate);
//        RLock lock = redissonClient.getLock("lock:order:" + userId);
//        //获取锁
//        boolean isLock = lock.tryLock();
//        if(!isLock) {
//            //获取锁失败 返回错误
//            return Result.fail("不允许重复下单");
//        }
//        try {
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.createVoucherOrder(voucherId);//这个函数其实是用this调用的 给省略了
//        }finally{
//            lock.unlock();
//        }
//    }
    /*
            1.tostring的底层每次调用都会创建一个全新的对象
             所以用intern返回字符串的规范表示
             也就是返回字符串常量池中的字符串地址
            2.如果把锁加在下面的方法上 不管是谁调用方法都要加这个锁 而且大家是同一把锁 意味着会串行执行 性能会差
            但是是同一个用户来了才会加锁 不同用户来了不会加锁 所以可以把锁加载查询到用户以后 用用户名加锁
            3.但是这样又有一个问题：在索释放以后 函数才结束 事务才提交 在释放锁但还没提交事务的过程中可能会出现其他线程进来产生冲突
            所以我们把锁加在调用函数方法这里 确保事务先提交再释放锁
            TODO 获取代理对象（保证事务生效)*/
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.createVoucherOrder(voucherId);//这个函数其实是用this调用的 给省略了
            /*
            4.this指的是非代理对象 即目标对象，没有事务功能 但是spring对createCoucherOrder做了动态代理 用他的代理对象去做事务处理
            这就是spring事务失效的几种可能性之一
            解决方法就是我们去获取spring的代理对象 用代理对象去调用这个函数 这里要注意的三点是：
            *目标对象是IVoucherOrder 给他生成代理对象
            *加上aopcontext的依赖
            *在application中加上注解enableAspectJAutoProxy 把exposeProxy改成true 暴露这个代理对象
            5.第五个问题 在集群环境下 这个锁只是针对一个jvm而不针对其他的jvm 所以如果一个人在两个端口下单依然是可以通过的
            为了解决 我们又采用分布式锁的方法
            */
    @Transactional
    @Nonnull
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        //5.一人一单
        //Long userId = UserHolder.getUser().getId();
        Long userId = voucherOrder.getUserId();//异步 子线程 只能通过voucherorder取
        //5.1查询订单
        Long count = query().eq("user_id", userId).eq("voucher_id", voucherOrder.getVoucherId()).count();
        //5.2判断是否已经存在
        if(count>0){
            //5.3用户已经购买过了 返回错误
            log.info("该用户已经购买过一次");
            return ;
        }
        //6.扣减库存
        boolean success=seckillVoucherService.update()
                .setSql("stock=stock-1")//更新语句
                .eq("voucher_id", voucherOrder.getVoucherId()).gt("stock",0)//where条件 id相等 (库存相等->库存大于零）（乐观锁）
                .update();
        if(!success){
            log.info("库存不足");
            return ;
        }
        //7.创建订单

        save(voucherOrder);//订单写入数据库
        //8.返回订单id

    }
}
