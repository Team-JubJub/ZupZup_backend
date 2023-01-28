package zupzup.back_end.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
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
    @PostMapping("/{storeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Long saveItem(@RequestPart(value = "item") ItemRequestDto requestDto,
                         @RequestPart(value = "image", required = false) MultipartFile itemImg) throws Exception {

        Long itemId = itemService.saveItem(requestDto, itemImg);
        return itemId;
    }

    @PutMapping("/{storeId}/{itemId}")
    public String updateItem(@RequestPart(value = "item") UpdateRequestDto updateDto,
                           @RequestPart(value = "image", required = false) MultipartFile itemImg) throws Exception {

        int i = itemService.updateItem(updateDto, itemImg);
        if(i == 0)
            return "업데이트 성공";
        else
            return "업데이트 실패";
    }

    @DeleteMapping("/{storeId}/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {

        itemService.deleteItem(itemId);
    }

    @PutMapping("/{storeId}/clear")
    public String clearCount(@PathVariable Long storeId) {

        try {
            itemService.clearCount(storeId);
            return "상품 개수를 초기화하였습니다.";
        } catch (Exception e) {
            return "상품 개수 초기화에 실패하였습니다.";
        }

    }
}