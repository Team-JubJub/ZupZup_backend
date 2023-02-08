package zupzup.back_end.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zupzup.back_end.store.dto.ItemDto;
import zupzup.back_end.store.dto.request.ItemRequestDto;
import zupzup.back_end.store.dto.request.UpdateRequestDto;
import zupzup.back_end.store.service.ItemService;

import java.io.IOException;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/seller")
public class ItemController {

    private final ItemService itemService;

    /**
     * 아이템 컨트롤러
     */
    @PostMapping("/{storeId}") // 사장님 메인 화면에서 상품 저장
    public ResponseEntity saveItem(@RequestPart(value = "item") ItemRequestDto requestDto,
                                   @RequestPart(value = "image", required = false) MultipartFile itemImg) throws Exception {

        Long itemId = itemService.saveItem(requestDto, itemImg);
        return new ResponseEntity(itemId, HttpStatus.CREATED); // 상품의 id 반환
    }

    @PutMapping("/{storeId}/{itemId}")
    public ResponseEntity updateItem(@PathVariable Long itemId,
                                     @RequestPart(value = "item") UpdateRequestDto updateDto,
                                     @RequestPart(value = "image", required = false) MultipartFile itemImg) throws Exception {


        String response = itemService.updateItem(itemId, updateDto, itemImg);

        return new ResponseEntity(response, HttpStatus.OK); //완료 여부 반환
    }

    @DeleteMapping("/{storeId}/{itemId}")
    public ResponseEntity deleteItem(@PathVariable Long itemId, @PathVariable String storeId) {

        String response = itemService.deleteItem(itemId);
        return new ResponseEntity(response, HttpStatus.OK); //삭제 여부 반환
    }

    @PutMapping("/{storeId}/clear")
    public ResponseEntity clearCount(@PathVariable Long storeId) {


        String response = itemService.clearCount(storeId);
        return new ResponseEntity(response, HttpStatus.OK); //초기화 여부 반환
    }
}