package com.rest.api.utils;

import com.rest.api.auth.jwt.JwtTokenProvider;
import domain.auth.User.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import repository.UserRepository;

@Component
@AllArgsConstructor
public class AuthUtils {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public User getUserEntity(String accessToken) {
        String providerUserId = jwtTokenProvider.getProviderUserId(accessToken);    // 유저의 id 조회
        User userEntity = userRepository.findByProviderUserId(providerUserId).get();

        return userEntity;
    }

}
