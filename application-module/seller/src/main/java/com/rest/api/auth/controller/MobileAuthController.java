package com.rest.api.auth.controller;

import com.rest.api.auth.service.MobileAuthService;
import domain.store.Store;
import dto.auth.seller.request.AuthRequestDto;
import dto.auth.seller.response.AuthResponseDto;
import dto.auth.token.RefreshResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/mobile")
public class MobileAuthController {

    private final MobileAuthService mobileAuthService;

    // < -------------- Sign-in part -------------- >
    @Operation(summary = "로그인", description = "로그인 성공 시 firebase에 저장된 가게의 키값을 리턴해줌.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = AuthRequestDto.SellerSignInDto.class))),
            @ApiResponse(responseCode = "400", description = "비밀번호가 다름",
                    content = @Content(schema = @Schema(implementation = AuthRequestDto.SellerSignInDto.class))),
            @ApiResponse(responseCode = "404", description = "찾는 유저가 없는 경우",
                    content = @Content(schema = @Schema(example = "No user found")))
    })
    @PostMapping("/sign-in")
    public ResponseEntity singIn(@RequestBody AuthRequestDto.SellerSignInDto sellerSignInDto) {
        AuthResponseDto.SignInResponseDto signInResponseDto = mobileAuthService.signIn(sellerSignInDto);
        if (signInResponseDto.getMessage().equals(mobileAuthService.LOGIN_FAILS)) {
            return new ResponseEntity(signInResponseDto, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(signInResponseDto, HttpStatus.OK);
    }

    // < -------------- Test part -------------- >
    @PostMapping("/test/sign-up")
    public ResponseEntity testSignUp(@RequestBody AuthRequestDto.SellerTestSingUpDto sellerTestSingUpDto) {
        Store storeEntity = mobileAuthService.testSignUp(sellerTestSingUpDto);

        return new ResponseEntity(storeEntity, HttpStatus.CREATED);
    }

}
