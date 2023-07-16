package com.rest.api.auth.service;

import com.rest.api.auth.dto.LoginInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import repository.SellerRepository;

@Service
@RequiredArgsConstructor
public class CustomSellerDetailsService { // jwtTokenProvider에서 user 정보 load 할 때 사용

    private final SellerRepository sellerRepository;

    public UserDetails loadSellerByLoginId(String loginId) {
        return new LoginInfoDto(sellerRepository.findSellerByLoginId(loginId));
    }

}
