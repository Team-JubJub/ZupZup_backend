package com.rest.api.store.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import dto.auth.seller.test.SellerTestSignInDto;
import dto.auth.seller.test.TestSignInResponseDto;
import dto.store.seller.request.PatchDto;
import dto.store.seller.response.Response;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rest.api.store.service.StoreService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Store", description = "가게와 관련된 API")
@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/seller")
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/{storeId}")
    public ResponseEntity storeDetails(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                           @PathVariable Long storeId) {
        return new ResponseEntity(storeService.storeDetails(storeId), HttpStatus.OK);
    }

    @Tag(name = "영업/휴무 설정", description = "영업/휴무 변경")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "휴무/영업 변경 완료(0-휴무, 1-영업)")
    )
    @PatchMapping("/open/{storeId}")
    public ResponseEntity changeIsOpened(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @PathVariable Long storeId,
                                         Boolean isOpened) {
        String isClosed = storeService.changeIsOpened(storeId, isOpened);
        return new ResponseEntity(isClosed, HttpStatus.OK);
    }

    @Tag(name = "공지 제외 수정", description = "가게 정보 관련 수정 중, 공지사항 제외 수정")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "수정된 정보 다시 리턴(확인용)")
    )
    @PatchMapping("/modification/{storeId}")
    public ResponseEntity modifyStore(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                      @PathVariable Long storeId,
                                      @RequestPart(name = "data") PatchDto patchDto,
                                      @RequestPart(name = "image", required = false) MultipartFile storeImg) throws IOException {

        Response response = storeService.modifyStore(storeId, patchDto, storeImg);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @Tag(name = "공지 수정", description = "가게 정보 관련 수정 중, 공지사항 수정")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "수정된 정보 다시 리턴(확인용)")
    )
    @PostMapping("/notice/{storeId}")
    public ResponseEntity changeNotification(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                             @PathVariable Long storeId,
                                             String storeMatters) {

        String isChanged = storeService.changeNotification(storeId, storeMatters);
        return new ResponseEntity(isChanged, HttpStatus.OK);
    }

    // For Test
    @PostMapping("/test/sign-in")
    public ResponseEntity testSignIn(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                     @RequestBody SellerTestSignInDto sellerTestSignInDto) {
        TestSignInResponseDto testSignInResponseDto = storeService.testSignIn(sellerTestSignInDto);

        return new ResponseEntity(testSignInResponseDto, HttpStatus.OK);
    }

}
