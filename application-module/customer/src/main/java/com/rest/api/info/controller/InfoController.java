package com.rest.api.info.controller;


import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.info.service.InfoService;
import dto.auth.customer.request.PatchNickNameDto;
import dto.auth.customer.response.PatchNicknameResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Log
@RequiredArgsConstructor
@RequestMapping("/info")
public class InfoController {

    private final InfoService infoService;

    @Operation(summary = "닉네임 수정", description = "유저의 닉네임 수정 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공",
                    content = @Content(schema = @Schema(implementation = PatchNicknameResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)"))),
            @ApiResponse(responseCode = "401", description = "로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @PatchMapping("/nickname")
    public ResponseEntity updateNickName(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @RequestBody PatchNickNameDto patchNickNameDto) {

        PatchNicknameResponseDto patchNicknameResponseDto = infoService.updateNickName(accessToken, patchNickNameDto);

        return new ResponseEntity(patchNicknameResponseDto, HttpStatus.OK);
    }

}
