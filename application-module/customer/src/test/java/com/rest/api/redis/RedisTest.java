package com.rest.api.redis;

import com.rest.api.auth.redis.RefreshTokenRepository;
import domain.auth.Token.RefreshToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.xml.datatype.Duration;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@DisplayName("Redis test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    private RefreshToken refreshToken;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

//    @BeforeEach
//    void setUp() {
//        refreshToken = new RefreshToken("test", "test123", 60);
//    }

    @Test
    void redisTest() {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set("test", "testvalue", 60, TimeUnit.SECONDS);
        String test = stringValueOperations.get("test");
        System.out.println(test);
        stringValueOperations.getAndDelete("test");
        System.out.println(stringValueOperations.get("test"));
//        if(test == null) {
//            System.out.println("is null");
//        }
//        System.out.println(test);
//        try {
//            Thread.sleep(30000);
//        } catch(InterruptedException e) {
//            System.out.println("Intercept");
//        }
//        String test2 = stringValueOperations.get("test");
//        if(test2 == null) {
//            System.out.println("expired");
//        }
//        else {
//            System.out.println(test2);
//        }
//        try {
//            Thread.sleep(5000);
//        } catch(InterruptedException e) {
//            System.out.println("Intercept");
//        }
//        System.out.println(stringValueOperations.get("test"));

//        refreshTokenRepository.save(refreshToken);
//        String refToken = refreshTokenRepository.findByRefreshToken("test123").getRefreshToken();
//        String providerUserId = refreshTokenRepository.findByProviderUserId("test").get().getProviderUserId();
//        System.out.println(refToken);
//        assertThat(refToken).isEqualTo(refreshToken.getRefreshToken());
//        assertThat(providerUserId).isEqualTo(refreshToken.getProviderUserId());
//        try {
//            Thread.sleep(30000);
//        } catch(InterruptedException e) {
//            System.out.println("Intercept");
//        }
//
//        String refToken2 = refreshTokenRepository.findByRefreshToken("test123").getRefreshToken();
//        System.out.println(refToken2);
//        assertThat(refToken2).isEqualTo(refreshToken.getRefreshToken());
//
//        try {
//            Thread.sleep(31000);
//        } catch(InterruptedException e) {
//            System.out.println("Intercept");
//        }
//
//        try {
//            String refToken3 = refreshTokenRepository.findByRefreshToken("test123").getRefreshToken();
//            assertThat(refToken3).isEqualTo(refreshToken.getRefreshToken());
//            System.out.println(refToken3);
//        } catch (NullPointerException e) {
//            System.out.println("expired");
//        }
    }

}
