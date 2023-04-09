package com.rest.api.auth.controller;

import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.service.MobileOAuthService;
import dto.auth.customer.request.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MobileOAuthController {
    /*
    Description
    모바일에서 유저 정보 전달(sign-in, POST) -> DB와 비교, 없으면 1. redirect(sign-up, GET), 있으면 2. redirect(main page)
    1. 회원가입으로 리더렉션, 다시 sign-up, POST를 통해 회원가입 -> 앱 사용 가능
    2. 앱 사용 가능
    */
    private final MobileOAuthService mobileOAuthService;

    @PostMapping("/sign-up/{provider}")    // 회원가입 요청
    public String signUp(@PathVariable String provider, @RequestHeader UserRequestDto.UserCheckDto userCheckDto, @RequestBody UserRequestDto.UserSignUpDto userSignUpDto) {   // ex) ~/sign-in/naver?access_token=...&refresh_token=... + body: { userUniqueId: "naver에서 준 ID" }
        String result = mobileOAuthService.signUp(provider, userCheckDto, userSignUpDto);

        return result;  // temp
    }
    @GetMapping("/sign-up") // 회원가입 페이지
    public String signUpPage() {
        return "Sign up page";
    }

    @PostMapping("/sign-in/{provider}")    // 로그인 요청, 최초 로그인에 사용할 예정
    public String signIn() {
        return "temp";
    }
    @GetMapping("/sign-in") // 로그인 페이지
    public String signInPage() {
        return "Sign in page";
    }


    // <----------- Test Controller ----------->
    @GetMapping("/login/oauth2/callback/naver") // -> 클라이언트가 구현할 파트
    public NaverLoginVo naverOAuthTestPage(@RequestParam Map<String, String> resValue) throws Exception {
        final NaverLoginVo naverLoginVo = mobileOAuthService.signInTest(resValue);

        return naverLoginVo;
    }
    @GetMapping("/sign-in/test")
    public String signInTestPage() {
        return "Sign in test page";
    }

}
