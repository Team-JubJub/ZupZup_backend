package com.rest.api.info.controller;


import com.rest.api.info.service.InfoService;
import com.zupzup.untact.model.dto.MessageDto;
import com.zupzup.untact.model.dto.info.customer.request.PatchNickNameDto;
import com.zupzup.untact.model.dto.info.customer.request.PatchOptionalTermDto;
import com.zupzup.untact.model.dto.info.customer.request.PatchPhoneNumberDto;
import com.zupzup.untact.model.dto.info.customer.response.GetInfoResponseDto;
import com.zupzup.untact.model.dto.info.customer.response.PatchInfoResponseDto;
import com.zupzup.untact.social.jwt.SocialJwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Info", description = "사용자의 정보와 관련된 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/info")
public class InfoController {

    private final InfoService infoService;

    // <-------------------- GET part -------------------->
    @Operation(summary = "유저의 정보 반환", description = "유저의 정보 반환 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 정보 반환 성공",
                    content = @Content(schema = @Schema(implementation = GetInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)"))),
            @ApiResponse(responseCode = "401", description = "로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @GetMapping("")
    public ResponseEntity getUserInfo(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken) {
        GetInfoResponseDto userInfoDto = infoService.getUserInfo(accessToken);

        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }


    // <-------------------- PATCH part -------------------->
    @Operation(summary = "전화번호 수정", description = "유저의 전화번호 수정 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전화번호 변경 성공",
                    content = @Content(schema = @Schema(implementation = PatchInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)"))),
            @ApiResponse(responseCode = "401", description = "로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @PatchMapping("/phone-number")
    public ResponseEntity updatePhoneNumber(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                            @Valid @RequestBody PatchPhoneNumberDto patchPhoneNumberDto) {
        PatchInfoResponseDto patchPhoneNumberResponseDto = infoService.updatePhoneNumber(accessToken, patchPhoneNumberDto);

        return new ResponseEntity(patchPhoneNumberResponseDto, HttpStatus.OK);
    }


    @Operation(summary = "닉네임 수정", description = "유저의 닉네임 수정 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공",
                    content = @Content(schema = @Schema(implementation = PatchInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)"))),
            @ApiResponse(responseCode = "401", description = "로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "Sign-outed or deleted user. Please sign-in or sign-up again."))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임",
                    content = @Content(schema = @Schema(example = "{\n\"message\" : \"Nickname conflicted.\"\n}")))
    })
    @PatchMapping("/nickname")
    public ResponseEntity updateNickName(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @RequestBody PatchNickNameDto patchNickNameDto) {

        PatchInfoResponseDto patchNicknameResponseDto = infoService.updateNickName(accessToken, patchNickNameDto);
        if (patchNicknameResponseDto == null) return new ResponseEntity(new MessageDto("Nickname conflicted."), HttpStatus.CONFLICT);

        return new ResponseEntity(patchNicknameResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "선택 약관 동의 여부 수정", description = "유저의 선택 약관 동의 여부 수정 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "선택 약관 동의 여부 변경 성공",
                    content = @Content(schema = @Schema(implementation = PatchInfoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)"))),
            @ApiResponse(responseCode = "401", description = "로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @PatchMapping("/optional-term")
    public ResponseEntity updateOptionalTerm(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                            @Valid @RequestBody PatchOptionalTermDto patchOptionalTermDto) {
        PatchInfoResponseDto patchOptionalResponseTermDto = infoService.updateOptionalTerm(accessToken, patchOptionalTermDto);

        return new ResponseEntity(patchOptionalResponseTermDto, HttpStatus.OK);
    }

}
