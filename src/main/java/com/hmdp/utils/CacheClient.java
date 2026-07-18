package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.entity.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.hmdp.utils.RedisConstants.*;

@Component
@Slf4j
public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit){
        //设置逻辑过期
        RedisData redisData=new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        //写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData),time,unit);
    }

    public <R,ID> R queryWithPassThrough(String keyPrefix, ID id, Class<R> type,
                                         Function<ID,R> dbFallback,Long time, TimeUnit unit){//function 有参有返回值 参数是id 返回值是r
        //1.从redis查询商户缓存
        String key=keyPrefix  + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断是否存在
        if(StrUtil.isNotBlank(json)) {//isNotBlank在为null “”  /t都返回flase
            //3.存在，直接返回
            return JSONUtil.toBean(json,type);
        }
        //TODO:为解决缓存穿透，需要再判断是否为空
        if(json!=null){
            //return Result.fail("该商户不存在");
            return null;
        }
        //4.不存在，根据id查询数据库
        R r = dbFallback.apply(id);
        //5.不存在，返回错误
        if(r==null){
            stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL, TimeUnit.MINUTES);
            //return Result.fail("店铺不存在");
            return null;
        }
        //6.存在，写入redis，返回
        this.set(key,r,time,unit);
        //7.返回
        return r;
    }


    private boolean tryLock(String key){
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(lock);
    }
    private void unlock(String key){
        stringRedisTemplate.delete(key);
    }
    private static final ExecutorService CACHE_REBUILD_EXECUTOR= Executors.newFixedThreadPool(10);

    /**
     * 逻辑过期解决缓存击穿
     * @param keyPrefix    缓存key前缀，如 cache:shop:
     * @param lockKeyPrefix 锁key前缀，如 lock:shop:
     * @param id           查询的id
     * @param type         返回值类型
     * @param dbFallback   数据库查询函数
     * @param time         逻辑过期时间
     * @param unit         时间单位
     */
    public <R,ID> R queryWithLogicalExpire(String keyPrefix, ID id, Class<R> type,
                                            Function<ID,R> dbFallback, Long time, TimeUnit unit){
        //1.从redis查询缓存
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断是否存在
        if(StrUtil.isBlank(json)) {
            //3.不存在，直接返回
            return null;
        }
        //4.命中，反序列化
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        JSONObject data = (JSONObject)redisData.getData();
        R r = JSONUtil.toBean(data, type);
        LocalDateTime expireTime = redisData.getExpireTime();
        //5.判断是否过期
        if(expireTime.isAfter(LocalDateTime.now())) {
            //5.1未过期，直接返回
            return r;
        }
        //5.2已过期，需要缓存重建
        //6.1获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        //6.2判断获取锁是否成功
        if(isLock){
            //6.3 双重检测：再读一次Redis，看是否已被其他线程重建
            String json2 = stringRedisTemplate.opsForValue().get(key);
            RedisData redisData2 = JSONUtil.toBean(json2, RedisData.class);
            if(redisData2.getExpireTime().isAfter(LocalDateTime.now())){
                //已被其他线程重建，返回新数据
                return JSONUtil.toBean((JSONObject) redisData2.getData(), type);
            }
            //6.4 仍过期，开独立线程重建缓存
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    //查数据库
                    R newR = dbFallback.apply(id);
                    //写入Redis（带逻辑过期时间）
                    this.setWithLogicalExpire(key, newR, time, unit);
                }catch(Exception e){
                    throw new RuntimeException(e);
                }finally {
                    //释放锁
                    unlock(lockKey);
                }
            });
        }
        //7.返回旧的缓存数据
        return r;
    }
}
