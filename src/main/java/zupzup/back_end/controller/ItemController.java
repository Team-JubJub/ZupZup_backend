package zupzup.back_end.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zupzup.back_end.dto.ItemDto;
import zupzup.back_end.service.ItemService;

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
    public void saveItem(@PathVariable Long storeId,
                         ItemDto itemDto,
                         @RequestParam("itemImg") MultipartFile itemImg) throws Exception {


        itemService.saveItem(itemDto, itemImg);
    }

    @PutMapping("/{storeId}")
    public void updateItem(@PathVariable Long storeId,
                           ItemDto itemDto, @Nullable MultipartFile itemImg) throws Exception {

        itemService.updateItem(itemDto, itemImg);
    }

    @DeleteMapping("/{storeId}/{itemId}")
    public void deleteItem(@PathVariable Long storeId,
                           @PathVariable Long itemId) {

        itemService.deleteItem(itemId);
    }

    @PutMapping("/{storeId}/clear")
    public void clearCount(@PathVariable Long storeId) {

        itemService.clearCount(storeId);
    }
}