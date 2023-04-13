package com.rest.api.auth.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.redis.RedisService;
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

@RestController
@RequestMapping("/mobile")
@RequiredArgsConstructor
public class MobileOAuthController {
    /*
    Description

    */
    private final MobileOAuthService mobileOAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
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
    public ResponseEntity signInWithRefreshToken(HttpServletResponse response, @CookieValue(value = JwtTokenProvider.ACCESS_TOKEN_NAME, required = false) String accessToken
            , @CookieValue(value = JwtTokenProvider.REFRESH_TOKEN_NAME, required = false) String refreshToken) {
        if (accessToken == null && refreshToken == null)    // 액세스, 리프레시 모두 만료인 상태로 요청이 들어왔을 경우
            return new ResponseEntity(new UserResponseDto.MessageDto("Access token and refresh token expired. Login required."), HttpStatus.UNAUTHORIZED);
        ValidRefreshTokenResponseDto result = jwtTokenProvider.validateRefreshToken(refreshToken);
        if (result.getStatus() == 200) {    // Refresh token 유효성 검증 성공
            Cookie accessTokenCookie = new Cookie(JwtTokenProvider.ACCESS_TOKEN_NAME, result.getAccessToken());
            accessTokenCookie.setMaxAge((int) (JwtTokenProvider.ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
            response.addCookie(accessTokenCookie);
            return new ResponseEntity(result, HttpStatus.OK);
        }
        return new ResponseEntity(new UserResponseDto.MessageDto("Refresh token validation failed. Login required."), HttpStatus.UNAUTHORIZED); // Refresh token 유효성 인증 실패
    }

    @PostMapping("/sign-in/{provider}")  // 로그인 요청(access, refresh token 모두 만료일 경우)
    public ResponseEntity signInWithProviderUserId(@PathVariable String provider, @RequestBody UserRequestDto.UserSignInDto userSignInDto, HttpServletResponse response) {
        TokenInfoDto reSignInResult = mobileOAuthService.signInWithProviderUserId(provider, userSignInDto);
        Cookie accessTokenCookie = new Cookie(JwtTokenProvider.ACCESS_TOKEN_NAME, reSignInResult.getAccessToken());   // 쿠키 set
        Cookie refreshTokenCookie = new Cookie(JwtTokenProvider.REFRESH_TOKEN_NAME, reSignInResult.getRefreshToken());
        accessTokenCookie.setMaxAge((int) (JwtTokenProvider.ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
        refreshTokenCookie.setMaxAge((int) (JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS / 1000));
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity(reSignInResult, HttpStatus.OK);
    }

    @PostMapping("/sign-out")
    public ResponseEntity signOut(@CookieValue(value = "accessToken", required = false) String accessToken, @CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (accessToken == null || !jwtTokenProvider.validateToken(accessToken) || refreshToken == null || !jwtTokenProvider.validateToken(refreshToken)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);    // Token 중 유효하지 않은 토큰이 하나라도 있으면 BAD_REQUEST 반환
        }
        Long remainExpiration = jwtTokenProvider.remainExpiration(accessToken); // 남은 expiration을 계산함.

        if (remainExpiration >= 1) {
            redisService.deleteToken(refreshToken); // refreshToken을 key로 하는 데이터 redis에서 삭제
            redisService.setStringValue(accessToken, "sign-out", remainExpiration); // access token 저장(key: acc_token, value: "sign-out")
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    // <----------- Test Controller ----------->
//    @GetMapping("/sign-in/oauth2/callback/naver") // -> 클라이언트가 구현할 파트
//    public NaverLoginVo naverOAuthTestPage(@RequestParam Map<String, String> resValue) throws Exception {
//        final NaverLoginVo naverLoginVo = mobileOAuthService.signInTestNaver(resValue);
//
//        return naverLoginVo;
//    }
    @GetMapping("/test/sign-in")
    public ResponseEntity signInTestPage(HttpServletRequest request) {
        System.out.println("Sign in test start");
        Cookie[] cookies = request.getCookies();
        UserDto userDto = mobileOAuthService.signInTestToken(cookies);

        return new ResponseEntity(userDto, HttpStatus.OK);
    }

}
