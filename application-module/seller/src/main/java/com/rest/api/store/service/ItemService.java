package com.rest.api.store.service;

import dto.item.seller.response.ItemResponseDto;
import repository.ItemRepository;
import repository.StoreRepository;
import domain.item.Item;
import domain.store.Store;
import dto.item.ItemDto;
import dto.item.seller.request.ItemRequestDto;
import dto.item.seller.request.UpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final StoreRepository storeRepository;
    private final S3Uploader s3Uploader;
    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public String saveItem(ItemRequestDto.postDto requestDto, MultipartFile itemImgFile, Long storeId) throws Exception {
        /**
         * 상품 등록
         * param: itemDto & multipartFile
         * return : void
         */

        //1. requestDto -> itemDto로 전환
        ItemDto.getDtoWithStore itemDto = new ItemDto.getDtoWithStore();
        itemDto.setItemName(requestDto.getItemName());
        itemDto.setItemPrice(requestDto.getItemPrice());
        itemDto.setSalePrice(requestDto.getSalePrice());
        itemDto.setItemCount(requestDto.getItemCount());

        System.out.println(storeRepository.findById(storeId));

        Store store = isStorePresent(storeId);
        itemDto.setStore(store);

        String imageURL = s3Uploader.upload(itemImgFile, store.getStoreName());
        itemDto.setImageURL(imageURL);

        // 2. DTO -> Entity
        Item item = new Item();
        item.saveItem(itemDto);

        //3. 상품 저장
        itemRepository.save(item);

        return item.getItemName();
    }

    @Transactional
    public String deleteItem(Long itemId) {
        /**
         * 상품 삭제
         * param : itemId
         * return : String
         */

        Item item = isItemPresent(itemId);

        itemRepository.deleteById(itemId);

        return "상품 삭제가 완료되었습니다.";
    }

    @Transactional
    public String updateItem(Long itemId, Long storeId, UpdateRequestDto updateDto, MultipartFile itemImg) throws IOException {
        // 1. 상품과 가게가 존재하는지
        Item itemEntity = isItemPresent(itemId);
        Store store = isStorePresent(storeId);

        // 2. 바뀐 이미지 체크해서 저장 (이미지가 없으면 빈 이미지로 저장)
        if(itemImg != null) {
            String imageURL = s3Uploader.upload(itemImg, store.getStoreName());
            updateDto.setImageURL(imageURL);
        } else {
            updateDto.setImageURL("");
        }

        // 3. 엔티티 업데이트
        itemEntity.updateItem(updateDto);
        return "상품 업데이트에 성공했습니다.";
    }

    public List<ItemDto.getDto> readItems(Long storeId) {

        Store store = storeRepository.findById(storeId).get();
        List<Item> itemList = itemRepository.findAllByStore(store);
        List<ItemDto.getDto> dtoList = new ArrayList<>();

        for(Item item : itemList) {

            ItemDto.getDto itemDto = modelMapper.map(item, ItemDto.getDto.class);
            dtoList.add(itemDto);
        }

        return dtoList;
    }

    public String modifyQuantity(Long storeId, List<ItemRequestDto.patchDto> quantityList) {

        for (ItemRequestDto.patchDto patchDto : quantityList) {

            Item item = itemRepository.findById(patchDto.getItemId()).get();
            item.setItemCount(patchDto.getItemCount());
            itemRepository.save(item);
        }

        return "수정이 완료되었습니다.";
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Item isItemPresent(Long itemId) {

        try {
            Item itemEntity = itemRepository.findById(itemId).get();
            return itemEntity;
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("해당 상품을 찾을 수 없습니다.");
        }
    }

    private Store isStorePresent(Long storeId) {

        try {
            Store store = storeRepository.findById(storeId).get();
            return store;
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("해당 가게를 찾을 수 없습니다.");
        }
    }
}