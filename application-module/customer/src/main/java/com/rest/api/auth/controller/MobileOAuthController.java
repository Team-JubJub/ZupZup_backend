package com.rest.api.auth.controller;

import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.service.MobileOAuthService;
import domain.auth.Provider;
import dto.auth.customer.request.TokenRequestDto;
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

    @PostMapping("/sign-up")    // 회원가입 요청
    public String signUp() {
        return "temp";
    }
    @GetMapping("/sign-up") // 회원가입 페이지
    public String signUpPage() {
        return "Sign up page";
    }

    @PostMapping("/sign-in/{provider}")    // 로그인 요청, 최초 로그인에 사용할 예정
    public String signIn(@PathVariable String provider, @RequestHeader TokenRequestDto tokenRequestDto) {   // ex) ~/sign-in/naver?access_token=...&refresh_token=... + body: { userUniqueId: "naver에서 준 ID" }
        String access_token = tokenRequestDto.getAccess_token();
        String refresh_token = tokenRequestDto.getRefresh_token();
        String userUniqueId = tokenRequestDto.getUserUniqueId();
        if(provider.equals(Provider.NAVER.getProvider().toLowerCase())) {
            String result = mobileOAuthService.naverOAuthSignIn(access_token, refresh_token, userUniqueId);
            if(result.equals("SignIn")) {   // 로그인 처리 -> jwt토큰 발급
                return "redirect:/sign-in/test";
            }
            else if(result.equals("SignUp")) {  // 회원가입 페이지로
                return "redirect:/sign-up";
            }
        }
        else if(provider.equals(Provider.KAKAO.getProvider().toLowerCase())) {

        }
        else if(provider.equals(Provider.APPLE.getProvider().toLowerCase())) {

        }

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
