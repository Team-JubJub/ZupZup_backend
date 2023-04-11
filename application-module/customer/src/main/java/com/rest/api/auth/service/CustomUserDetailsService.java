package com.rest.api.auth.service;

import com.rest.api.auth.dto.LoginInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByProviderUserId(String providerUserId) {
        return new LoginInfoDto(userRepository.findByProviderUserId(providerUserId).get());
    }

}