package com.rest.api.auth.service;

import domain.auth.Token.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repository.RefreshTokenRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RefreshTokenRepository refreshTokenRepository;

    public List<String> getListValue(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
        String getRefreshToken = refreshTokenEntity.getRefreshToken();
        String getProviderUserId = refreshTokenEntity.getProviderUserId();
        List<String> findInfo = new ArrayList<>();
        findInfo.add(getProviderUserId);
        findInfo.add(getRefreshToken);

        return findInfo;
    }

    public RefreshToken getStringValue(String accessToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByAccessToken(accessToken);

        return refreshTokenEntity;
    }

    public void deleteToken(String refreshToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);

    }

}
