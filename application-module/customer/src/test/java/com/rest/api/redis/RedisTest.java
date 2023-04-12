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

import static org.assertj.core.api.FactoryBasedNavigableListAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

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

    @Test
    void redisTest() {
        refreshTokenRepository.save(refreshToken);
        String refToken = refreshTokenRepository.findByRefreshToken("test123").getRefreshToken();
        String providerUserId = refreshTokenRepository.findByProviderUserId("test").get().getProviderUserId();
        assertThat(refToken).isEqualTo(refreshToken.getRefreshToken());
        assertThat(providerUserId).isEqualTo(refreshToken.getProviderUserId());
        try {
            Thread.sleep(30000);
        } catch(InterruptedException e) {
            System.out.println("Intercept");
        }

        String refToken2 = refreshTokenRepository.findByRefreshToken("test123").getRefreshToken();
        assertThat(refToken2).isEqualTo(refreshToken.getRefreshToken());

        try {
            Thread.sleep(31000);
        } catch(InterruptedException e) {
            System.out.println("Intercept");
        }

        try {
            String refToken3 = refreshTokenRepository.findByRefreshToken("test123").getRefreshToken();
            assertThat(refToken3).isEqualTo(refreshToken.getRefreshToken());
        } catch (NullPointerException e) {
            System.out.println("expired");
        }
    }

}
