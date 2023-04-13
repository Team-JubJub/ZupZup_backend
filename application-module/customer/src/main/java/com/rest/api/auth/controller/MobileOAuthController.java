package com.rest.api.auth.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.service.MobileOAuthService;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.UserRequestDto;
import dto.auth.customer.response.UserResponseDto;
import dto.auth.token.TokenInfoDto;
import dto.auth.token.ValidRefreshTokenResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider jwtTokenProvider;
    // < -------------- Sign up part -------------- >
    @PostMapping("/sign-up/{provider}")    // 회원가입 요청
    public ResponseEntity signUp(@PathVariable String provider, @RequestBody UserRequestDto.UserSignUpDto userSignUpDto, HttpServletResponse response) {   // ex) ~/sign-in/naver?access_token=...&refresh_token=... + body: { userUniqueId: "naver에서 준 ID" }
        TokenInfoDto signUpResult = mobileOAuthService.signUp(provider, userSignUpDto); // service layer에서 user 정보 저장, refresh token redis에 저장까지
        Cookie accessTokenCookie = new Cookie(JwtTokenProvider.ACCESS_TOKEN_NAME, signUpResult.getAccessToken());   // 쿠키 set
        Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_NAME, signUpResult.getRefreshToken());
         accessTokenCookie.setMaxAge((int) (JwtTokenProvider.ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
         refreshTokenCookie.setMaxAge((int) (JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
        // accessTokenCookie.setSecure(true);
        // accessTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true);
        // refreshTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(signUpResult, HttpStatus.CREATED);  // temp
    }

    @PostMapping("/sign-in/refresh")    // 로그인 요청(access token 만료, refresh token 유효할 경우)  -> 추후에 파라미터 CookieValue말고 HttpServletRequest로 바꾸는 것 고민해볼 것
    public ResponseEntity signInWithRefreshToken(HttpServletResponse response, @CookieValue(value = JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken
            , @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_NAME) String refreshToken) {
        if (accessToken == null || refreshToken == null)
            return new ResponseEntity<>(new UserResponseDto.MessageDto("redirect: /mobile/sign-in/{provider} with provider's access token and provider's user unique ID"), HttpStatus.UNAUTHORIZED);   // 소셜에 인증을 거쳐 로그인하는 곳으로 redirect
        ValidRefreshTokenResponseDto result = jwtTokenProvider.validateRefreshToken(accessToken, refreshToken);
        if (result.getStatus() == 200) {
            Cookie accessTokenCookie = new Cookie(JwtTokenProvider.ACCESS_TOKEN_NAME, result.getAccessToken());
            accessTokenCookie.setMaxAge((int) (JwtTokenProvider.ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
            response.addCookie(accessTokenCookie);
            return new ResponseEntity(result, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/sign-in/{provider}")  // 로그인 요청(access, refresh token 모두 만료일 경우)
    public ResponseEntity signInWithProviderRequest(@PathVariable String provider, @RequestBody UserRequestDto.UserSignInDto userSignInDto, HttpServletResponse response) {
        TokenInfoDto reSignInResult = mobileOAuthService.signInWithProviderRequest(provider, userSignInDto);
        Cookie accessTokenCookie = new Cookie(JwtTokenProvider.ACCESS_TOKEN_NAME, reSignInResult.getAccessToken());   // 쿠키 set
        Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_NAME, reSignInResult.getRefreshToken());
        accessTokenCookie.setMaxAge((int) (JwtTokenProvider.ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
        refreshTokenCookie.setMaxAge((int) (JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(reSignInResult, HttpStatus.OK);
    }


    // <----------- Test Controller ----------->
    @GetMapping("/sign-in/oauth2/callback/naver") // -> 클라이언트가 구현할 파트
    public NaverLoginVo naverOAuthTestPage(@RequestParam Map<String, String> resValue) throws Exception {
        final NaverLoginVo naverLoginVo = mobileOAuthService.signInTestNaver(resValue);

        return naverLoginVo;
    }
    @GetMapping("/test/sign-in")
    public ResponseEntity signInTestPage(HttpServletRequest request) {
        System.out.println("Sign in test start");
        Cookie[] cookies = request.getCookies();
        UserDto userDto = mobileOAuthService.signInTestToken(cookies);

        return new ResponseEntity(userDto, HttpStatus.OK);
    }

}
