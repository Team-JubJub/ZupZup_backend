package com.rest.api.store.controller;

import com.rest.api.store.service.StoreService;
import dto.store.customer.response.GetStoreDetailsDto;
import dto.store.customer.response.GetStoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/customer/store")
public class StoreController {

    private final StoreService storeService;

    // <-------------------- GET part -------------------->
    @GetMapping("") // 가게들 list
    public ResponseEntity storeList(@RequestParam(required = false) String storeName) {
        if(storeName != null) { // 가게 검색건이 있을 경우
            List<GetStoreDto> searchedStoreDtoList = storeService.searchedStoreList(storeName);
            if (searchedStoreDtoList.size() == 0) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(searchedStoreDtoList, HttpStatus.OK);
        }
        else {  // 가게 검색건이 없는 경우
            List<GetStoreDto> allStoreDtoList = storeService.storeList();
            if (allStoreDtoList.size() == 0) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(allStoreDtoList, HttpStatus.OK);
        }
    }

    @GetMapping("/{category}") // 카테고리별 가게 조회
    public ResponseEntity storeListByCategory(@PathVariable String category) {
        List<GetStoreDto> allStoreDtoByCategoryList = storeService.storeListByCategory(category);
        if (allStoreDtoByCategoryList.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(allStoreDtoByCategoryList, HttpStatus.OK);
    }

    @GetMapping("/{storeId}") // 가게 상세 화면
    public ResponseEntity storeDetail(@PathVariable Long storeId) {

        GetStoreDetailsDto storeDetailDto = storeService.storeDetail(storeId);

        if(storeDetailDto.getItemDtoList().size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(storeDetailDto, HttpStatus.OK);
    }

}
