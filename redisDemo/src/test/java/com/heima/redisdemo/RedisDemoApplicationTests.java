package com.heima.redisdemo;

import com.heima.redisdemo.redis.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
class RedisDemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    void testString() {
        redisTemplate.opsForValue().set("name","huge");
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println("name="+name);
    }
    @Test
    void testSavaUser(){
        //写入数据
        redisTemplate.opsForValue().set("user:100",new User("huge",21));
        //获取数据
        //User o=(User)redisTemplate.opsForValue().get("user:100");
        Object obj = redisTemplate.opsForValue().get("user:100");
        ObjectMapper objectMapper = new ObjectMapper();
        User o = objectMapper.convertValue(obj, User.class);
        System.out.println("o="+o);

    }
}
