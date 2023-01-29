package zupzup.back_end.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;
import zupzup.back_end.store.dto.ItemDto;
import zupzup.back_end.store.dto.request.ItemRequestDto;
import zupzup.back_end.store.dto.request.UpdateRequestDto;
import zupzup.back_end.store.repository.ItemRepository;
import zupzup.back_end.store.repository.StoreRepository;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    private final S3Uploader s3Uploader;
    @Autowired
    ModelMapper modelMapper;

    @Transactional
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
        System.out.println(itemDto.getItemCount());
        Store store = storeRepository.findById(requestDto.getStoreId())
                .orElseThrow(EntityNotFoundException::new);
        itemDto.setStore(store);

        String imageURL = s3Uploader.upload(itemImgFile, store.getStoreName());
        itemDto.setImageURL(imageURL);

        Item item = new Item();
        item.saveItem(itemDto);

        itemRepository.save(item);
        System.out.println(item.getItemCount());

        return item.getItemId();
    }

    @Transactional
    public void deleteItem(Long itemId) {
        /**
         * 상품 삭제
         * param : itemId
         * return : void
         */

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        itemRepository.deleteById(itemId);
    }

    @Transactional
    public void clearCount(Long storeId) {
        /**
         * 상품 개수 초기화
         * param : storeId
         * return : void
         */

        Store store = storeRepository.findById(storeId).get();
        List<Item> itemList = itemRepository.findAllByStore(store);

        for(Item item : itemList) {

            ItemDto itemDto = new ItemDto();
            itemDto.toItemDto(item);
            itemDto.setItemCount(0);

            item.saveItem(itemDto);
        }
    }

    @Transactional
    public int updateItem(UpdateRequestDto updateDto, MultipartFile itemImg) throws Exception {

        try {
            Item itemEntity = itemRepository.findById(updateDto.getItemId()).get();
            Store store = storeRepository.findById(updateDto.getStoreId()).get();

            if(itemImg != null) {
                String imageURL = s3Uploader.upload(itemImg, store.getStoreName());
                updateDto.setImageURL(imageURL);
            } else {
                updateDto.setImageURL("");
            }

            //modelMapper.map(updateDto, itemEntity);
            itemEntity.updateItem(updateDto);

            return 0;
        } catch (IOException e) {
            return 1;
        }

    }
}