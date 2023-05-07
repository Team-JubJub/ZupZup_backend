package com.rest.api.auth.service;

import dto.auth.seller.request.AuthRequestDto;
import dto.auth.seller.response.AuthResponseDto;

public class MobileAuthService {

    public AuthResponseDto.SignInResponseDto signIn(AuthRequestDto.SellerSignInDto sellerSignInDto) {
        String loginId = sellerSignInDto.getLoginId();
        AuthResponseDto.SignInResponseDto signInResponseDto = new AuthResponseDto.SignInResponseDto();


        return signInResponseDto;
    }

}
