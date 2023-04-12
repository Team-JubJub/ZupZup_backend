package com.rest.api.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


import java.util.concurrent.TimeUnit;


@DisplayName("Redis test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void redisTest() {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set("eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2ODEyODg4MzAsImV4cCI6MTY4MjQ5ODQzMH0.xCPzYdOFFigZoItSj-XRxfa1mfcIG8L0EJDsG32GTMU", "testvalue", 60, TimeUnit.SECONDS);
        String test = stringValueOperations.get("eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2ODEyODg4MzAsImV4cCI6MTY4MjQ5ODQzMH0.xCPzYdOFFigZoItSj-XRxfa1mfcIG8L0EJDsG32GTMU");
        System.out.println(test);
        stringValueOperations.getAndDelete("eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2ODEyODg4MzAsImV4cCI6MTY4MjQ5ODQzMH0.xCPzYdOFFigZoItSj-XRxfa1mfcIG8L0EJDsG32GTMU");
        System.out.println(stringValueOperations.get("test"));
    }

}
