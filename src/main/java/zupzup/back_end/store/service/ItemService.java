package zupzup.back_end.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;
import zupzup.back_end.store.dto.ItemDto;
import zupzup.back_end.store.dto.StoreDto;
import zupzup.back_end.store.dto.request.ItemRequestDto;
import zupzup.back_end.store.repository.ItemRepository;
import zupzup.back_end.store.repository.StoreRepository;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    @Autowired
    private S3Uploader s3Uploader;

    public Long saveItem(ItemRequestDto requestDto, MultipartFile itemImgFile) throws Exception {
        /**
         * 상품 등록
         * param: itemDto & multipartFile
         * return : void
         */

        //상품 등록
        ItemDto itemDto = new ItemDto();
        itemDto.setItemName(requestDto.getItemName());
        itemDto.setItemPrice(requestDto.getItemPrice());
        itemDto.setSalePrice(requestDto.getSalePrice());
        itemDto.setItemCount(requestDto.getItemCount());
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(EntityNotFoundException::new);
        itemDto.setStore(store);

        if(!itemImgFile.isEmpty()) {
            String imageURL = s3Uploader.upload(itemImgFile, store.getStoreName());
            itemDto.setImageURL(imageURL);
        }

        Item item = itemDto.createItem();

        itemRepository.save(item);

        return item.getItemId();
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
    }

    public void updateItem(ItemDto itemDto, MultipartFile itemImg) throws Exception {

    }
}