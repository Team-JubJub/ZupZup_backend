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

@DisplayName("Redis test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        refreshToken = new RefreshToken("test", "test123", 60);
    }

    @AfterEach
    void teardown() {

    }

    @Test


}
