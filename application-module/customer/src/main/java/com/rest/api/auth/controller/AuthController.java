package com.rest.api.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    /*
    Description
    모바일에서 유저 정보 전달(sign-in, POST) -> DB와 비교, 없으면 1. redirect(sign-up, GET), 있으면 2. redirect(main page)
    1. 회원가입으로 리더렉션, 다시 sign-up, POST를 통해 회원가입 -> 앱 사용 가능
    2. 앱 사용 가능
    */
    @PostMapping("/sign-up")    // 회원가입 요청
    public String signUp() {
        return "temp";
    }
    @GetMapping("/sign-up") // 회원가입 페이지
    public String signUpPage() {
        return "temp";
    }

    @PostMapping("/sign-in")    // 로그인 요청
    public String signIn() {
        return "temp";
    }
    @GetMapping("/sign-in") // 로그인 페이지
    public String signInPage() {
        return "temp";
    }


}
