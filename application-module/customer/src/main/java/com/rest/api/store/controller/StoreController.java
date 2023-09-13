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
    @GetMapping("") // 카테고리별 가게 조회
    public ResponseEntity storeList(@RequestParam(required = false) String category) {
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

    @GetMapping("/{storeId}") // 가게 상세 화면
    public ResponseEntity storeDetail(@PathVariable Long storeId) {

        GetStoreDetailsDto storeDetailDto = storeService.storeDetail(storeId);

        if(storeDetailDto.getItemDtoList().size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(storeDetailDto, HttpStatus.OK);
    }

}
