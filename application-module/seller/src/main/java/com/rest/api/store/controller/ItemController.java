package com.rest.api.store.controller;

import dto.item.seller.request.ItemRequestDto;
import dto.item.seller.request.UpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.rest.api.store.service.ItemService;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/seller")
public class ItemController {

    private final ItemService itemService;

    /**
     * 아이템 컨트롤러
     */
    @PostMapping("/{storeId}") // 상품 저장
    public ResponseEntity saveItem(@RequestPart(value = "item") ItemRequestDto requestDto,
                                   @RequestPart(value = "image", required = false) MultipartFile itemImg,
                                   @PathVariable Long storeId) throws Exception {

        String itemName = itemService.saveItem(requestDto, itemImg, storeId);
        String format = String.format("상품 %s(이)가 저장되었습니다.", itemName);
        return new ResponseEntity(format, HttpStatus.CREATED); // 상품의 이름 반환
    }

    @PatchMapping("/{storeId}/{itemId}")
    public ResponseEntity updateItem(@PathVariable Long itemId,
                                     @PathVariable Long storeId,
                                     @RequestPart(value = "item") UpdateRequestDto updateDto,
                                     @RequestPart(value = "image", required = false) MultipartFile itemImg) throws Exception {


        String response = itemService.updateItem(itemId, storeId, updateDto, itemImg);

        return new ResponseEntity(response, HttpStatus.OK); //완료 여부 반환
    }

    @DeleteMapping("/{storeId}/{itemId}")
    public ResponseEntity deleteItem(@PathVariable Long itemId, @PathVariable String storeId) {

        String response = itemService.deleteItem(itemId);
        return new ResponseEntity(response, HttpStatus.OK); //삭제 여부 반환
    }
}