package com.rest.api.info.controller;


import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.info.service.InfoService;
import dto.auth.customer.request.PatchNickNameDto;
import dto.auth.customer.response.PatchNicknameResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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

    @PatchMapping("/nickname")
    public ResponseEntity updateNickName(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @RequestBody PatchNickNameDto patchNickNameDto) {

        PatchNicknameResponseDto patchNicknameResponseDto = infoService.updateNickName(accessToken, patchNickNameDto);

        return new ResponseEntity(patchNicknameResponseDto, HttpStatus.OK);
    }

}
