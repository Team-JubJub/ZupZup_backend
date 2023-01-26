package zupzup.back_end.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zupzup.back_end.store.dto.ItemDto;
import zupzup.back_end.store.dto.request.ItemRequestDto;
import zupzup.back_end.store.service.ItemService;

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
    public void saveItem(@RequestPart(value = "item") ItemRequestDto requestDto,
                         @RequestPart(value = "image") MultipartFile itemImg) throws Exception {

        itemService.saveItem(requestDto, itemImg);
    }

    @PutMapping("/{storeId}")
    public void updateItem(@RequestPart(value = "item") ItemDto itemDto,
                           @Nullable @RequestPart(value = "image") MultipartFile itemImg) throws Exception {

        itemService.updateItem(itemDto, itemImg);
    }

    @DeleteMapping("/{storeId}/{itemId}")
    public void deleteItem(@PathVariable Long itemId) {

        itemService.deleteItem(itemId);
    }

    @PutMapping("/{storeId}/clear")
    public void clearCount(@PathVariable Long storeId) {

        itemService.clearCount(storeId);
    }
}