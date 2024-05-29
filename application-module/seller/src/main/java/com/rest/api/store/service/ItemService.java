package com.rest.api.store.service;

import com.rest.api.aws.S3Uploader;
import com.rest.api.utils.FCMUtils;
import com.zupzup.untact.model.domain.item.Item;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.dto.item.seller.request.PatchItemCountDto;
import com.zupzup.untact.dto.item.seller.request.PostItemDto;
import com.zupzup.untact.dto.item.seller.request.UpdateRequestDto;
import com.zupzup.untact.dto.item.seller.response.GetDto;
import com.zupzup.untact.dto.item.seller.response.GetDtoWithStore;
import com.zupzup.untact.dto.item.seller.response.ItemResponseDto;
import com.zupzup.untact.repository.ItemRepository;
import com.zupzup.untact.repository.StoreRepository;
import com.zupzup.untact.exception.item.seller.NoSuchItemException;
import com.zupzup.untact.exception.store.seller.NoSuchStoreException;
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
    private final FCMUtils fcmUtils;
    @Autowired
    ModelMapper modelMapper;

    @Transactional
    public ItemResponseDto saveItem(PostItemDto requestDto, MultipartFile itemImgFile, Long storeId) throws Exception {
        /**
         * 상품 등록
         * param: itemDto & multipartFile
         * return : void
         */

        //1. requestDto -> itemDto로 전환
        GetDtoWithStore itemDto = new GetDtoWithStore();
        itemDto.setItemName(requestDto.getItemName());
        itemDto.setItemPrice(requestDto.getItemPrice());
        itemDto.setSalePrice(requestDto.getSalePrice());
        itemDto.setItemCount(requestDto.getItemCount());

        Store store = isStorePresent(storeId);
        itemDto.setStore(store);

        if(itemImgFile != null) {
            String imageURL = s3Uploader.upload(itemImgFile, store.getStoreName());
            itemDto.setImageURL(imageURL);
        } else {
            itemDto.setImageURL("");
        }

        // 2. DTO -> Entity
        Item item = new Item();
        item.saveItem(itemDto);

        //3. 상품 저장
        itemRepository.save(item);

        //4. 푸시 알림 전송
        fcmUtils.sendMessageToAlertUsers(storeId, "신규 상품 등록 알림", store.getStoreName() + "에 신규 상품 " + requestDto.getItemName() + "이/가 등록되었어요!");

        return modelMapper.map(item, ItemResponseDto.class);
    }

    public List<GetDto> readItems(Long storeId) {

        Store store = isStorePresent(storeId);
        List<Item> itemList = itemRepository.findAllByStore(store);
        List<GetDto> dtoList = new ArrayList<>();

        for(Item item : itemList) {

            GetDto itemDto = modelMapper.map(item, GetDto.class);
            dtoList.add(itemDto);
        }

        return dtoList;
    }

    @Transactional
    public ItemResponseDto updateItem(Long itemId, Long storeId, UpdateRequestDto updateDto, MultipartFile itemImg) throws IOException {
        // 1. 가게와 상품이 존재하는지
        Store store = isStorePresent(storeId);
        Item itemEntity = isItemPresent(itemId);

        // 2. 바뀐 이미지 체크해서 저장 (이미지가 없으면 빈 이미지로 저장)
        if(itemImg != null) {
            String imageURL = s3Uploader.upload(itemImg, store.getStoreName());
            updateDto.setImageURL(imageURL);
        } else {    // 변경할 이미지를 보내지 않았을 때
            updateDto.setImageURL(itemEntity.getImageURL());    // 기존의 이미지를 사용하도록 수정
        }

        // 3. 엔티티 업데이트
        itemEntity.updateItem(updateDto);
        itemRepository.save(itemEntity);

        return modelMapper.map(itemEntity, ItemResponseDto.class);
    }

    public String modifyQuantity(Long storeId, List<PatchItemCountDto> quantityList) {

        for (PatchItemCountDto patchItemCountDto : quantityList) {
            Item item = isItemPresent(patchItemCountDto.getItemId());
            item.setItemCount(patchItemCountDto.getItemCount());
            itemRepository.save(item);
        }

        return "수정이 완료되었습니다.";
    }

    @Transactional
    public String deleteItem(Long itemId) {
        /**
         * 상품 삭제
         * param : itemId
         * return : String
         */

        Item item = isItemPresent(itemId);

        itemRepository.deleteById(item.getId());

        return "상품 삭제가 완료되었습니다.";
    }


    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Item isItemPresent(Long itemId) {

        try {
            Item itemEntity = itemRepository.findById(itemId).get();
            return itemEntity;
        } catch (NoSuchElementException e) {
            throw new NoSuchItemException("해당 상품을 찾을 수 없습니다.");
        }
    }

    private Store isStorePresent(Long storeId) {

        try {
            Store store = storeRepository.findById(storeId).get();
            return store;
        } catch (NoSuchElementException e) {
            throw new NoSuchStoreException("해당 가게를 찾을 수 없습니다.");
        }
    }
}