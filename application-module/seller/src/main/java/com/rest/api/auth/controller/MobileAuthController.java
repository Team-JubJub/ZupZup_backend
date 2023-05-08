package com.rest.api.auth.controller;

import com.rest.api.auth.service.MobileAuthService;
import domain.store.Store;
import dto.auth.seller.request.AuthRequestDto;
import dto.auth.seller.response.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/mobile")
public class MobileAuthController {

    private final MobileAuthService mobileAuthService;

    // < -------------- Sign-in part -------------- >
    @PostMapping("/sign-in")
    public ResponseEntity singIn(@RequestBody AuthRequestDto.SellerSignInDto sellerSignInDto) {
        AuthResponseDto.SignInResponseDto signInResponseDto = mobileAuthService.signIn(sellerSignInDto);
        if (signInResponseDto.getMessage().equals(mobileAuthService.LOGIN_FAILS)) {
            return new ResponseEntity(signInResponseDto, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(signInResponseDto, HttpStatus.OK);
    }

    // < -------------- Test part -------------- >
    @PostMapping("/test/sign-up")
    public ResponseEntity testSignUp(@RequestBody AuthRequestDto.SellerTestSingUpDto sellerTestSingUpDto) {
        Store storeEntity = mobileAuthService.testSignUp(sellerTestSingUpDto);

        return new ResponseEntity(storeEntity, HttpStatus.CREATED);
    }

}
