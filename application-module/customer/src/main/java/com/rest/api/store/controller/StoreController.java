package com.rest.api.store.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.store.service.StoreService;
import dto.order.customer.response.PostOrderResponseDto;
import dto.store.customer.response.GetStoreDetailsDto;
import dto.store.customer.response.GetStoreDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Store", description = "사용자의 가게 조회와 관련된 API")
@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/customer/store")
public class StoreController {

    private final StoreService storeService;

    // <-------------------- GET part -------------------->
    @Operation(summary = "유저의 주문 요청", description = "유저의 주문 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 성공",
                    content = @Content(schema = @Schema(implementation = PostOrderResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "가게 조회 요청은 성공했으나 조회된 가게가 0개인 경우"),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @GetMapping("") // 카테고리별 가게 조회
    public ResponseEntity storeList(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                    @RequestParam(required = false) String category) {
        if(category != null) { // 카테고리 선택 시(우리는 카테고리 선택을 통해 조회하는 것이 메인 기능임)
            List<GetStoreDto> allStoreDtoByCategoryList = storeService.storeListByCategory(category);
            if (allStoreDtoByCategoryList.size() == 0) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(allStoreDtoByCategoryList, HttpStatus.OK);
        }
        else {  // 카테고리 선택안했을 시, 전체 가게 리턴
            List<GetStoreDto> allStoreDtoList = storeService.storeList();
            if (allStoreDtoList.size() == 0) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(allStoreDtoList, HttpStatus.OK);
        }
    }

    @Operation(summary = "가게 상세 조회 요청", description = "가게 상세 조회 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = PostOrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @GetMapping("/{storeId}") // 가게 상세 조회
    public ResponseEntity storeDetails(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                      @PathVariable Long storeId) {
        GetStoreDetailsDto storeDetailsDto = storeService.storeDetails(storeId);

        return new ResponseEntity(storeDetailsDto, HttpStatus.OK);
    }

}
