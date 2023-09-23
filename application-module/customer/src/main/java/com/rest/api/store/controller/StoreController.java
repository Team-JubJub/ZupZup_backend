package com.rest.api.store.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.store.service.StoreService;
import dto.order.customer.response.GetOrderDetailsDto;
import dto.order.customer.response.PostOrderResponseDto;
import dto.store.customer.response.GetStoreDetailsDto;
import dto.store.customer.response.GetStoreDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
    @Operation(summary = "카테고리 별 가게 조회 요청", description = "카테고리별 가게 조회 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 조회 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = GetStoreDetailsDto.class)))
                    }
            ),
            @ApiResponse(responseCode = "204", description = "가게 조회 요청은 성공했으나 조회된 가게가 0개인 경우"),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @GetMapping("") // 카테고리별 가게 조회
    public ResponseEntity storeList(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                    @Parameter(name = "category", description = "조회할 카테고리(전체 조회할 시에는 쿼리 파트 없이 ~/store 로 요청)", in = ParameterIn.QUERY) @RequestParam(required = false) String category) {
        if(category != null) { // 카테고리 선택 시(우리는 카테고리 선택을 통해 조회하는 것이 메인 기능임)
            List<GetStoreDetailsDto> allStoreDtoByCategoryList = storeService.storeListByCategory(category);
            if (allStoreDtoByCategoryList.size() == 0) return new ResponseEntity(HttpStatus.NO_CONTENT);

            return new ResponseEntity(allStoreDtoByCategoryList, HttpStatus.OK);
        }
        else {  // 카테고리 선택안했을 시, 전체 가게 리턴
            List<GetStoreDetailsDto> allStoreDtoList = storeService.storeList();
            if (allStoreDtoList.size() == 0) return new ResponseEntity(HttpStatus.NO_CONTENT);

            return new ResponseEntity(allStoreDtoList, HttpStatus.OK);
        }
    }

    @Operation(summary = "찜한 가게 조회 요청", description = "찜한 가게 조회 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "찜한 가게 조회 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = GetStoreDetailsDto.class)))
                    }
            ),
            @ApiResponse(responseCode = "204", description = "가게 조회 요청은 성공했으나 조회된 가게가 0개인 경우"),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @GetMapping("/starred")
    public ResponseEntity starredStoreList(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken) {
        List<GetStoreDetailsDto> allStoreDtoByStarredList = storeService.starredStoreList(accessToken);
        if (allStoreDtoByStarredList.size() == 0) return new ResponseEntity(HttpStatus.NO_CONTENT);

        return new ResponseEntity(allStoreDtoByStarredList, HttpStatus.OK);
    }

    @Operation(summary = "가게 상세 조회 요청", description = "가게 상세 조회 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "가게 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = GetStoreDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again."))),
            @ApiResponse(responseCode = "404", description = "해당 가게를 찾을 수 없음",
                    content = @Content(schema = @Schema(example = "해당 가게를 찾을 수 없습니다.")))
    })
    @GetMapping("/{storeId}") // 가게 상세 조회
    public ResponseEntity storeDetails(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                      @PathVariable Long storeId) {
        GetStoreDetailsDto storeDetailsDto = storeService.storeDetails(accessToken, storeId);

        return new ResponseEntity(storeDetailsDto, HttpStatus.OK);
    }

}
