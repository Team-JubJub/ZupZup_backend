package com.rest.api.auth.controller;

import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.naver.vo.NaverProfileVo;
import com.rest.api.auth.service.MobileOAuthService;
import domain.auth.Provider;
import dto.auth.customer.request.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/sign-up")    // 회원가입 요청
    public String signUp() {
        return "temp";
    }
    @GetMapping("/sign-up") // 회원가입 페이지
    public String signUpPage() {
        return "Sign up page";
    }

    @PostMapping("/sign-in/{provider}")    // 로그인 요청
    public String signIn(@PathVariable String provider, @RequestParam String access_token, @RequestParam String refresh_token
            , @RequestBody UserRequestDto.UserOAuthSignInDto userOAuthSignInDto) {   // ex) ~/sign-in/naver?access_token=...&refresh_token=... + body: { userUniqueId: "naver에서 준 ID" }
        if(provider.equals(Provider.NAVER)) {
            String result = mobileOAuthService.naverOAuthSignIn(access_token, refresh_token, userOAuthSignInDto);
            if(result.equals("SignIn")) {   // 로그인 처리 -> jwt토큰 발급
                return "redirect:/sign-in/test";
            }
            else if(result.equals("SignUp")) {  // 회원가입 페이지로
                return "redirect:/sign-up";
            }
        }
        else if(provider.equals(Provider.KAKAO)) {

        }
        else if(provider.equals(Provider.APPLE)) {

        }

        return "temp";
    }
    @GetMapping("/sign-in") // 로그인 페이지
    public String signInPage() {
        return "Sign in page";
    }

    @GetMapping("/login/oauth2/callback/naver")
    public @ResponseBody NaverLoginVo naverOAuthTestPage(@RequestParam Map<String, String> resValue) throws Exception {
        System.out.println(resValue);
        final NaverLoginVo naverLoginVo = mobileOAuthService.signInTest(resValue);

        return naverLoginVo;
    }
    @GetMapping("/sign-in/test")
    public String signInTestPage() {
        return "Sign in test page";
    }

}
