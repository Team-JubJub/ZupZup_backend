package com.rest.api.auth.controller;

import com.rest.api.auth.service.MobileAuthService;
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

    @PostMapping("/sign-in")
    public ResponseEntity singIn(@RequestBody AuthRequestDto.SellerSignInDto sellerSignInDto) {
        AuthResponseDto.SignInResponseDto signInResponseDto = mobileAuthService.signIn(sellerSignInDto);

        return new ResponseEntity(HttpStatus.OK);
    }
}
