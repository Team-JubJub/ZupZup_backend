package zupzup.back_end.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.ItemImg;
import zupzup.back_end.store.dto.ItemDto;
import zupzup.back_end.store.dto.StoreDto;
import zupzup.back_end.store.repository.ItemRepository;
import zupzup.back_end.store.repository.StoreRepository;

import java.util.List;

@Service
@Log
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final StoreRepository storeRepository;

    public void saveItem(ItemDto itemDto, MultipartFile itemImgFile) throws Exception {
        /**
         * 상품 등록
         * param: itemDto & multipartFile
         * return : void
         */

        //상품 등록
        Item item = itemDto.createItem();
        itemRepository.save(item);

        //이미지 등록
        ItemImg itemImg = new ItemImg();
        itemImg.setItem(item);

        itemImgService.saveItemImg(itemImg, itemImgFile);
    }

    public void deleteItem(Long itemId) {
        /**
         * 상품 삭제
         * param : itemId & storeId
         * return : void
         */

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        if(item == null) {
            throw new EntityNotFoundException();
        } else itemRepository.deleteById(itemId);
    }

    public void clearCount(Long storeId) {
        /**
         * 상품 개수 초기화
         * param : storeId
         * return : void
         */

        StoreDto storeDto = StoreDto.of(storeRepository.findById(storeId)
                .orElseThrow(EntityNotFoundException::new));

        List<Item> itemList = storeDto.getStoreItems();

        for(int i=0; i<itemList.size(); i++) {
            itemList.get(i).setCount(0);
        }
    }

    public void updateItem(ItemDto itemDto, MultipartFile itemImg) throws Exception {

        //상품 수정
        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemDto);
        Long itemImgId = itemDto.getItemImgDto().getId();

        //이미지 등록
        itemImgService.updateItemImg(itemImgId, itemImg);
    }
}