package com.rest.api.store.controller;

import dto.auth.seller.request.SellerRequestDto;
import dto.auth.seller.response.SellerResponseDto;
import dto.store.seller.request.StoreRequestDto;
import dto.store.seller.response.StoreResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import com.rest.api.store.service.StoreService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/seller")
public class StoreController {

    private final StoreService storeService;

    /**
     * 메인 페이지(관리 관련) 컨트롤러
     */

    /*@GetMapping("/{storeId}")
    public StoreResponseDto managementMain(@PathVariable Long storeId) {
        // 가게 관련 내용 (가게 이름 및 운영 시간, 이벤트 내용, 오늘 할인 시간)
        // 제품 관련 내용 ([제품 이미지, 제품 이름, 가격])
        // Store 관련 DTO 전체 넘김
        return storeService.mainPage(storeId);
    }*/

    @PatchMapping("/open/{storeId}")
    public ResponseEntity changeIsOpened(@PathVariable Long storeId,
                                         Boolean isOpened) {

        String isClosed = storeService.changeIsOpened(storeId, isOpened);
        return new ResponseEntity(isClosed, HttpStatus.OK);
    }

    @PatchMapping("/modification/{storeId}")
    public ResponseEntity modifyStore(@PathVariable Long storeId,
                                      @RequestPart(name = "data") StoreRequestDto.patchDto patchDto,
                                      @RequestPart(name = "image") @Nullable MultipartFile storeImg) throws IOException {

        StoreResponseDto.response response = storeService.modifyStore(storeId, patchDto, storeImg);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/notice/{storeId}")
    public ResponseEntity changeNotification(@PathVariable Long storeId,
                                             String storeMatters) {

        String isChanged = storeService.changeNotification(storeId, storeMatters);
        return new ResponseEntity(isChanged, HttpStatus.OK);
    }

    // For Test
    @PostMapping("/test/sign-in")
    public ResponseEntity testSignIn(@RequestBody SellerRequestDto.SellerTestSignInDto sellerTestSignInDto) {
        SellerResponseDto.TestSignInResponseDto testSignInResponseDto = storeService.testSignIn(sellerTestSignInDto);

        return new ResponseEntity(testSignInResponseDto, HttpStatus.OK);
    }

}
