package com.rest.api.store.controller;

import com.rest.api.store.service.StoreService;
import dto.store.customer.response.StoreResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/customer/store")
public class StoreController {

    private final StoreService storeService;

    // <-------------------- GET part -------------------->
    @GetMapping("") // 가게들 list
    public ResponseEntity storeList() {
        List<StoreResponseDto.GetStoreDto> allStoreListDto = storeService.storeList();
        if(allStoreListDto.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(allStoreListDto, HttpStatus.OK);
    }

    @GetMapping("/{storeId}") // 가게 상세 화면
    public ResponseEntity storeDetail(@PathVariable Long storeId) {

        StoreResponseDto.GetStoreDetailDto storeDetailDto = storeService.storeDetail(storeId);

        if(storeDetailDto.getItemDtoList().size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(storeDetailDto, HttpStatus.OK);
    }

}
