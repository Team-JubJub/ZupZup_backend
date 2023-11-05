package com.rest.api.utils;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.zupzup.untact.domain.auth.User.User;
import com.zupzup.untact.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
