package com.hmdp.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //开始时间戳
    private static final long BEGIN_TIMESTAMP=1767225600;

    //序列号的位数
    private static final int COUNT_BITS=32;


    public long nextId(String keyPrefix){

        //1.生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timeStamp=nowSecond-BEGIN_TIMESTAMP;

        //2.生成序列号
        //2.1获取当天日期 精确到天
        String date = now.format(DateTimeFormatter.ofPattern("yyyy：MM：dd"));//1.避免超过32bit上限2.方便统计
        //2.2自增长
        Long count = stringRedisTemplate.opsForValue().increment("irc:" + keyPrefix + ":" + date);
        //3.拼接并返回
        return timeStamp<<COUNT_BITS|count;

    }
    public static void main(String[] args){
        LocalDateTime time = LocalDateTime.of(2026, 1, 1, 0, 0);
        long second = time.toEpochSecond(ZoneOffset.UTC);
        System.out.println("second="+second);
    }
}
