package com.rest.api.auth.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.redis.RedisService;
import com.rest.api.auth.service.MobileOAuthService;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.UserRequestDto;
import dto.auth.customer.response.UserResponseDto;
import dto.auth.token.TokenInfoDto;
import dto.auth.token.RefreshResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증과 관련된 API")
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
    @Operation(summary = "회원가입", description = "회원가입 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    headers = {@Header(name = JwtTokenProvider.ACCESS_TOKEN_NAME, description = "액세스 토큰"),
                            @Header(name = JwtTokenProvider.REFRESH_TOKEN_NAME, description = "리프레시 토큰")},
                    content = @Content(schema = @Schema(implementation = TokenInfoDto.class))),
            @ApiResponse(responseCode = "409", description = "(다른 소셜 플랫폼을 이용하여)이미 가입된 유저",
                    content = @Content(schema = @Schema(example = "User already sign uped.(Platform with: NAVER)")))
    })
    @PostMapping("/sign-up/{provider}")    // 회원가입 요청
    public ResponseEntity signUp(@Parameter(name = "provider", description = "소셜 플랫폼 종류(소문자)", in = ParameterIn.PATH,
            content = @Content(schema = @Schema(type = "string", allowableValues = {"naver", "kakao", "google", "apple"}))) @PathVariable String provider,
                                 @RequestBody UserRequestDto.UserSignUpDto userSignUpDto) {   // ex) ~/sign-in/naver?access_token=...&refresh_token=... + body: { userUniqueId: "naver에서 준 ID" }
        TokenInfoDto signUpResult = mobileOAuthService.signUp(provider, userSignUpDto); // service layer에서 user 정보 저장, refresh token redis에 저장까지
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(jwtTokenProvider.ACCESS_TOKEN_NAME, signUpResult.getAccessToken());
        responseHeaders.set(jwtTokenProvider.REFRESH_TOKEN_NAME, signUpResult.getRefreshToken());

        return new ResponseEntity(signUpResult, responseHeaders, HttpStatus.CREATED);  // temp
    }

    // < -------------- Sign in part -------------- >
    @Operation(summary = "로그인(리프레시 토큰 유효 시)", description = "리프레시 토큰을 이용한 액세스 토큰 갱신")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스 토큰 갱신 성공",
                    headers = {@Header(name = JwtTokenProvider.ACCESS_TOKEN_NAME, description = "액세스 토큰")},
                    content = @Content(schema = @Schema(implementation = RefreshResultDto.class))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰의 유효성 인증이 실패한 경우",
                    content = @Content(schema = @Schema(example = "Refresh token validation failed. Login required")))
    })
    @PostMapping("/sign-in/refresh")    // 로그인 요청(access token 만료, refresh token 유효할 경우), refresh token만 받아옴
    public ResponseEntity signInWithRefreshToken(@Parameter(name = JwtTokenProvider.ACCESS_TOKEN_NAME, description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
            @Parameter(name = JwtTokenProvider.REFRESH_TOKEN_NAME, description = "리프레시 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.REFRESH_TOKEN_NAME) String refreshToken) {
        RefreshResultDto refreshResult = jwtTokenProvider.validateRefreshToken(accessToken, refreshToken);   // refresh token 유효성 검증
        if (refreshResult.getResult().equals(jwtTokenProvider.SUCCESS_STRING)) {    // Refresh token 유효성 검증 성공 시 헤더에 액세스 토큰, 바디에 result, message, id, 토큰 전달
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(jwtTokenProvider.ACCESS_TOKEN_NAME, refreshResult.getAccessToken());

            return new ResponseEntity(refreshResult, responseHeaders, HttpStatus.OK);
        }

        return new ResponseEntity(new UserResponseDto.MessageDto("Refresh token validation failed. Login required"), HttpStatus.UNAUTHORIZED); // Refresh token 유효성 인증 실패
    }

    @Operation(summary = "로그인(모든 토큰 만료 시)", description = "소셜 플랫폼에 재로그인을 통해 받아온 user unique ID를 이용, 액세스와 리프레시 토큰 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스, 리프레시 토큰 재발급(로그인) 성공",
                    headers = {@Header(name = JwtTokenProvider.ACCESS_TOKEN_NAME, description = "액세스 토큰"),
                            @Header(name = JwtTokenProvider.REFRESH_TOKEN_NAME, description = "리프레시 토큰")},
                    content = @Content(schema = @Schema(implementation = TokenInfoDto.class)))
    })
    @PostMapping("/sign-in/{provider}")  // 로그인 요청(access, refresh token 모두 만료일 경우)
    public ResponseEntity signInWithProviderUserId(@Parameter(name = "provider", description = "소셜 플랫폼 종류(소문자)", in = ParameterIn.PATH,
            content = @Content(schema = @Schema(type = "string", allowableValues = {"naver", "kakao", "google", "apple"}))) @PathVariable String provider,
            @RequestBody UserRequestDto.UserSignInDto userSignInDto) {
        TokenInfoDto reSignInResult = mobileOAuthService.signInWithProviderUserId(provider, userSignInDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(jwtTokenProvider.ACCESS_TOKEN_NAME, reSignInResult.getAccessToken());
        responseHeaders.set(jwtTokenProvider.REFRESH_TOKEN_NAME, reSignInResult.getRefreshToken());

        return new ResponseEntity(reSignInResult, responseHeaders, HttpStatus.OK);
    }

    // < -------------- Sign out part -------------- >
    @Operation(summary = "로그아웃", description = "로그아웃 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                content = @Content(schema = @Schema(example = "Sign-out successful"))),
            @ApiResponse(responseCode = "400", description = "정보가 잘못된 토큰",
                content = @Content(schema = @Schema(example = "Access token invalid"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 직전(1초 미만), 로그아웃 처리도 없이 토큰 만료 시간 경과로 처리",
                content = @Content(schema = @Schema(example = "Access token expired")))
    })
    @PostMapping("/sign-out")
    public ResponseEntity signOut(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                  @Parameter(name = "refreshToken", description = "리프레시 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.REFRESH_TOKEN_NAME) String refreshToken) {
        if (accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
            return new ResponseEntity(new UserResponseDto.MessageDto("Access token invalid"), HttpStatus.BAD_REQUEST);    // access token 정보가 잘못된 형식이라면
        }
        Long remainExpiration = jwtTokenProvider.remainExpiration(accessToken); // 남은 expiration을 계산함.

        if (remainExpiration >= 1) {
            redisService.deleteKey(refreshToken); // refreshToken을 key로 하는 데이터 redis에서 삭제
            redisService.setStringValue(accessToken, "sign-out", remainExpiration); // access token 저장(key: acc_token, value: "sign-out")
            return new ResponseEntity(new UserResponseDto.MessageDto("Sign-out successful"), HttpStatus.OK);
        }
        return new ResponseEntity(new UserResponseDto.MessageDto("Access token expired"), HttpStatus.UNAUTHORIZED);
    }

    // <----------- Test Controller ----------->
    @Operation(summary = "김영후의 테스트용 컨트롤러")
    @GetMapping("/test/sign-in")
    public ResponseEntity signInTestPage(@RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @RequestHeader(JwtTokenProvider.REFRESH_TOKEN_NAME) String refreshToken) {
        System.out.println("Sign in test start");
        UserDto userDto = mobileOAuthService.signInTestToken(accessToken, refreshToken);

        return new ResponseEntity(userDto, HttpStatus.OK);
    }

}
