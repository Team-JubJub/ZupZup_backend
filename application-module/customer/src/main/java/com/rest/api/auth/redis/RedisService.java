package com.rest.api.auth.redis;

import domain.auth.Token.RefreshToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public List<String> getListValue(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        String getRefreshToken = refreshTokenEntity.getRefreshToken();
        String getProviderUserId = refreshTokenEntity.getProviderUserId();
        List<String> findInfo = new ArrayList<>();
        findInfo.add(getProviderUserId);
        findInfo.add(getRefreshToken);

        return findInfo;
    }

    @Transactional
    public String getStringValue(String accessToken) {  // redis에 accessToken 저장돼있는지(로그아웃인지) 판단
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByAccessToken(accessToken);
        String foundAccessToken = refreshTokenEntity.getAccessToken();

        return foundAccessToken;
    }

    @Transactional
    public void setStringValue(String token, String data, Long expirationTime) {
        if(data.equals("sign-out")) {    // 로그아웃 상황에 data를 "sign-out"로 줌.
            String accessToken = token;
            Long remainExpirationTime = expirationTime; // 로그아웃 시 access token의 남은 유효시간
            LocalDateTime expiredAt = LocalDateTime.now().plusSeconds((int) (expirationTime / 1000));  // 밀리초 단위이므로 나누기 100해줌
            System.out.println(expiredAt);  // For test
            Integer expiration = (int) (expirationTime / 1);

            refreshTokenRepository.save(RefreshToken.builder()
                    .accessToken(accessToken)
                    .expiration(expiration)
                    .build());
        }
        else {  // 로그인 상황에 data는 providerUserId
            String refreshToken = token;
            String providerUserId = data;
            LocalDateTime expiredAt = LocalDateTime.now().plusSeconds((int) (expirationTime / 1000));
            System.out.println(expiredAt);  // For test
            Integer expiration = (int) (expirationTime / 1);

            refreshTokenRepository.save(RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .providerUserId(providerUserId)
                    .expiration(expiration)
                    .build());
        }
    }

    @Transactional
    public void deleteToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
    }

}
