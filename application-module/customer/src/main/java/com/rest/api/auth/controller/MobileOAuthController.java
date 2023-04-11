package com.rest.api.auth.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.service.MobileOAuthService;
import com.rest.api.auth.redis.RedisService;
import dto.auth.customer.request.UserRequestDto;
import dto.auth.token.TokenInfoDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mobile")
@RequiredArgsConstructor
public class MobileOAuthController {
    /*
    Description

    */
    private final MobileOAuthService mobileOAuthService;
    private final RedisService redisService;
    // < -------------- Sign up part -------------- >
    @PostMapping("/sign-up/{provider}")    // 회원가입 요청
    public ResponseEntity signUp(@PathVariable String provider, @RequestBody UserRequestDto.UserSignUpDto userSignUpDto, HttpServletResponse response) {   // ex) ~/sign-in/naver?access_token=...&refresh_token=... + body: { userUniqueId: "naver에서 준 ID" }
        TokenInfoDto signUpResult = mobileOAuthService.signUp(provider, userSignUpDto); // service layer에서 user 정보 저장, refresh token redis에 저장까지
        Cookie accessTokenCookie = new Cookie(JwtTokenProvider.ACCESS_TOKEN_NAME, signUpResult.getAccessToken());   // 쿠키 set
        Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_NAME, signUpResult.getRefreshToken());
         accessTokenCookie.setMaxAge((int) JwtTokenProvider.ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS);
        // accessTokenCookie.setSecure(true);
        // accessTokenCookie.setHttpOnly(true);
         refreshTokenCookie.setMaxAge((int) JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        // refreshTokenCookie.setSecure(true);
        // refreshTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(signUpResult, HttpStatus.CREATED);  // temp
    }

    @PostMapping("/sign-in/refresh")    // 로그인 요청(refresh token 유효할 경우)
    public ResponseEntity refresh(HttpServletResponse response,
            @CookieValue(value = "accessToken") String accessToken
            , @CookieValue(value = "refreshToken") String refreshToken) {
        if (accessToken == null || refreshToken == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        ValidRefreshTokenResponse result = jwtTokenProvider.validateRefreshToken(accessToken, refreshToken);
        if (result.getStatus() == 200) {
            response.addCookie((new Cookie("accessToken", result.getAccessToken())));
            return new ResponseEntity(result, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/refresh")
    public ResponseEntity reissueRefreshToken() {

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
