package com.rest.api.auth.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.redis.RedisService;
import com.rest.api.auth.service.MobileAuthService;
import domain.auth.Seller.Seller;
import dto.MessageDto;
import dto.auth.seller.request.SellerSignInDto;
import dto.auth.seller.test.SellerTestSignUpDto;

import dto.auth.token.seller.SellerRefreshResultDto;
import dto.auth.token.seller.SellerTokenInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증과 관련된 API")
@RestController
@RequestMapping("/mobile")
@RequiredArgsConstructor
public class MobileAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final MobileAuthService mobileAuthService;

    // < -------------- Sign-in part -------------- >
    @Operation(summary = "로그인(모든 토큰 만료 시)", description = "Login ID를 이용, 액세스와 리프레시 토큰 재발급")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스, 리프레시 토큰 재발급(로그인) 성공",
                    headers = {@Header(name = JwtTokenProvider.ACCESS_TOKEN_NAME, description = "액세스 토큰"),
                            @Header(name = JwtTokenProvider.REFRESH_TOKEN_NAME, description = "리프레시 토큰")},
                    content = @Content(schema = @Schema(implementation = SellerTokenInfoDto.class))),
            @ApiResponse(responseCode = "400", description = "Request body 파라미터가 잘못된 경우, Request body의 값이 유효셩에 어긋나는 경우",
                    content = @Content(schema = @Schema(example = "Required request body is missing\n or \n" +
                            "{\n" +
                            "\t\"userUniqueId\": \"User unique id cannot be null or empty or space\"\n" +
                            "}"))),
            @ApiResponse(responseCode = "403", description = "아이디를 통한 로그인 시 비밀번호가 틀린 경우",
                    content = @Content(schema = @Schema(example = "{\n" +
                            "\t\"result\": \"Login fails\",\n" +
                            "\t\"message\": \"Wrong password\",\n" +
                            "\t\"accessToken\": null,\n" +
                            "\t\"refreshToken\": null,\n" +
                            "\t\"storeId\": null\n" +
                            "}"))),
            @ApiResponse(responseCode = "404", description = "제공된 login ID를 가진 사장님 조회가 불가능한 경우(login ID가 잘못된 경우)",
                    content = @Content(schema = @Schema(example = "Seller with ID doesn't present")))
    })
    @PostMapping("/sign-in")  // 로그인 요청(access, refresh token 모두 만료일 경우)
    public ResponseEntity signInWithSellerLoginId(@Valid @RequestBody SellerSignInDto sellerSignInDto) {
        SellerTokenInfoDto reSignInResult = mobileAuthService.signInWithSellerLoginId(sellerSignInDto);
        if (reSignInResult.getResult().equals(mobileAuthService.LOGIN_FAILS)) return new ResponseEntity(reSignInResult, HttpStatus.FORBIDDEN);   // 비밀번호 틀렸을 경우

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(jwtTokenProvider.ACCESS_TOKEN_NAME, reSignInResult.getAccessToken());
        responseHeaders.set(jwtTokenProvider.REFRESH_TOKEN_NAME, reSignInResult.getRefreshToken());

        return new ResponseEntity(reSignInResult, responseHeaders, HttpStatus.OK);
    }

    @Operation(summary = "로그인(리프레시 토큰 유효 시)", description = "리프레시 토큰을 이용한 액세스 토큰 갱신")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스 토큰 갱신 성공",
                    headers = {@Header(name = JwtTokenProvider.ACCESS_TOKEN_NAME, description = "액세스 토큰")},
                    content = @Content(schema = @Schema(implementation = SellerRefreshResultDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(리프레시 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required request header 'refreshToken' for method parameter type String is not present"))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰의 유효성 인증이 실패한 경우",
                    content = @Content(schema = @Schema(example = "Refresh token validation failed. Login required")))
    })
    @PostMapping("/sign-in/refresh")    // 로그인 요청(access token 만료, refresh token 유효할 경우), refresh token만 받아옴
    public ResponseEntity signInWithRefreshToken(@Parameter(name = JwtTokenProvider.REFRESH_TOKEN_NAME, description = "리프레시 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.REFRESH_TOKEN_NAME) String refreshToken) {
        SellerRefreshResultDto refreshResult = jwtTokenProvider.validateRefreshToken(refreshToken);   // refresh token 유효성 검증
        if (refreshResult.getResult().equals(jwtTokenProvider.SUCCESS_STRING)) {    // Refresh token 유효성 검증 성공 시 헤더에 액세스 토큰, 바디에 result, message, id, 토큰 전달
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(jwtTokenProvider.ACCESS_TOKEN_NAME, refreshResult.getAccessToken());

            return new ResponseEntity(refreshResult, responseHeaders, HttpStatus.OK);
        }

        return new ResponseEntity("Refresh token validation failed. Login required", HttpStatus.UNAUTHORIZED); // Refresh token 유효성 인증 실패
    }

    // < -------------- Sign-out part -------------- >
    @Operation(summary = "로그아웃", description = "로그아웃 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(example = "{\n\"message\" : \"Sign-out successful\"\n}"))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(리프레시 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required request header 'refreshToken' for method parameter type String is not present"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user."))),
            @ApiResponse(responseCode = "403", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits")))
    })
    @PostMapping("/sign-out")
    public ResponseEntity signOut(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                  @Parameter(name = "refreshToken", description = "리프레시 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.REFRESH_TOKEN_NAME) String refreshToken) {
        String result = mobileAuthService.signOut(accessToken, refreshToken);
        if (result.equals("success")) return new ResponseEntity(new MessageDto("Sign-out successful"), HttpStatus.OK);

        return new ResponseEntity("redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)", HttpStatus.UNAUTHORIZED);
    }

    // < -------------- Account recovery part -------------- >
//    @Operation(summary = "계정 찾기", description = "휴대폰 번호 인증을 통해 가입시 이용한 플랫폼 리턴")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "가입시 이용한 플랫폼 리턴",
//                    content = @Content(schema = @Schema(example = "{\n\"message\" : \"NAVER\"\n}"))),
//            @ApiResponse(responseCode = "400", description = "Request body 파라미터가 잘못된 경우",
//                    content = @Content(schema = @Schema(example = "Required request body is missing"))),
//            @ApiResponse(responseCode = "400", description = "Request body의 값이 유효셩에 어긋나는 경우",
//                    content = @Content(schema = @Schema(example = "{\n" +
//                            "\t\"phoneNumber\": \"Phone number pattern should be like 010-xxxx-xxxx\"\n" +
//                            "}"))),
//            @ApiResponse(responseCode = "404", description = "해당 유저는 가입한 적이 없음(자원 없음)",
//                    content = @Content(schema = @Schema(example = "{\n\"message\" : \"No user found\"\n}")))
//    })
//    @PostMapping("/account-recovery")
//    public ResponseEntity accountRecovery(@Valid @RequestBody UserRequestDto.AccountRecoveryDto accountRecoveryDto) {
//        String result = mobileAuthService.accountRecovery(accountRecoveryDto);
//        if(result.equals(mobileAuthService.NO_USER_FOUND)) {
//            return new ResponseEntity(new UserResponseDto.MessageDto(result), HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity(new UserResponseDto.MessageDto(result), HttpStatus.OK);
//    }

    // < -------------- Test Part -------------- >
    @PostMapping("/test/sign-up")
    public ResponseEntity testSignUp(@RequestBody SellerTestSignUpDto sellerTestSignUpDto) {
        Seller sellerEntity = mobileAuthService.testSignUp(sellerTestSignUpDto);

        return new ResponseEntity(sellerEntity, HttpStatus.OK);
    }


}
