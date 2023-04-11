package com.rest.api.auth.service;

import domain.auth.Token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    RefreshToken findByAccessToken(String accessToken);
    RefreshToken findByRefreshToken(String refreshToken);
    Optional<RefreshToken> findByProviderUserId(String providerUserId);

}
