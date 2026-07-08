package com.heima.redisdemo;

import com.heima.redisdemo.redis.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
class RedisStringTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    void testString() {
        stringRedisTemplate.opsForValue().set("name","huge");
        Object name = stringRedisTemplate.opsForValue().get("name");
        System.out.println("name="+name);
    }
    private static final ObjectMapper mapper=new ObjectMapper();
    @Test
    void testSavaUser(){
        //创建对象
        User user=new User("胡歌",21);
        //手动序列化
        String json = mapper.writeValueAsString(user);
        //写入数据
        stringRedisTemplate.opsForValue().set("user:200",json);
        //获取数据
        //User o=(User)redisTemplate.opsForValue().get("user:100");
        String jsonUser = stringRedisTemplate.opsForValue().get("user:200");
        //手动反序列化
        User user1 = mapper.readValue(jsonUser, User.class);
        System.out.println("user1="+user1);
//        ObjectMapper objectMapper = new ObjectMapper();
//        User o = objectMapper.convertValue(obj, User.class);
//        System.out.println("o="+o);

    }
}
