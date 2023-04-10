package com.rest.api.auth.controller;

import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.service.MobileOAuthService;
import dto.auth.customer.request.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MobileOAuthController {
    /*
    Description

    */
    private final MobileOAuthService mobileOAuthService;
    // < -------------- Sign up part -------------- >
    @PostMapping("/sign-up/{provider}")    // 회원가입 요청
    public ResponseEntity signUp(@PathVariable String provider, @RequestBody UserRequestDto.UserSignUpDto userSignUpDto) {   // ex) ~/sign-in/naver?access_token=...&refresh_token=... + body: { userUniqueId: "naver에서 준 ID" }
        String result = mobileOAuthService.signUp(provider, userSignUpDto);

        return new ResponseEntity("temp", HttpStatus.CREATED);  // temp
    }

    @PostMapping("/sign-in/")    // 로그인 요청(토큰 없을 경우)
    public ResponseEntity signIn() {

        return new ResponseEntity("temp", HttpStatus.OK);
    }

    // <----------- Test Controller ----------->
    @GetMapping("/login/oauth2/callback/naver") // -> 클라이언트가 구현할 파트
    public NaverLoginVo naverOAuthTestPage(@RequestParam Map<String, String> resValue) throws Exception {
        final NaverLoginVo naverLoginVo = mobileOAuthService.signInTest(resValue);

        return naverLoginVo;
    }
    @GetMapping("/test/sign-in")
    public String signInTestPage() {
        return "Sign in test page";
    }

}
