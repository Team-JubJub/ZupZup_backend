package com.rest.api.auth.service;

import com.rest.api.auth.dto.LoginInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService { // jwtTokenProvider에서 user 정보 load 할 때 사용

    private final UserRepository userRepository;

    public UserDetails loadUserByProviderUserId(String providerUserId) {
        return new LoginInfoDto(userRepository.findByProviderUserId(providerUserId).get());
    }

}
