package com.rest.api.store.service;

import com.zupzup.untact.domain.item.Item;
import com.zupzup.untact.domain.store.Store;
import com.zupzup.untact.dto.item.seller.response.ItemResponseDto;
import com.zupzup.untact.dto.store.customer.response.GetStoreDetailsDto;
import com.zupzup.untact.dto.store.customer.response.GetStoreDto;
import com.zupzup.untact.repository.ItemRepository;
import com.zupzup.untact.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class  StoreService {

    @Autowired
    ModelMapper modelMapper;
    private final StoreRepository storeRepository;
    private final ItemRepository itemRepository;

    // <-------------------- GET part -------------------->
    public List<GetStoreDto> storeList() {   // 현재는 예외처리할 것 없어 보임
        List<Store> allStoreListEntity = storeRepository.findAll(); // 나중에는 위치기반 등으로 거르게 될 듯?
        List<GetStoreDto> allStoreListDto = allStoreListEntity.stream()
                .map(m -> modelMapper.map(m, GetStoreDto.class))
                .collect(Collectors.toList());

        return allStoreListDto;
    }

    public List<GetStoreDto> searchedStoreList(String storeName) {
        List<Store> searchedStoreListEntity = storeRepository.findByStoreNameContaining(storeName);
        List<GetStoreDto> searchedStoreListDto = searchedStoreListEntity.stream()
                .map(m -> modelMapper.map(m, GetStoreDto.class))
                .collect(Collectors.toList());

        return searchedStoreListDto;
    }

    public GetStoreDetailsDto storeDetail(Long storeId) {

        //store entity 가져와서 DTO로 변환
        Store storeEntity = storeRepository.findById(storeId).get();

        GetStoreDetailsDto storeDetailDto
                = modelMapper.map(storeEntity, GetStoreDetailsDto.class);

        //item list 생성 및 StoreDto에 저장
        List<Item> itemList = itemRepository.findAllByStore(storeEntity);
        List<ItemResponseDto> itemDtoList = new ArrayList<>();

        for(Item item : itemList) {

            ItemResponseDto itemResponseDto = new ItemResponseDto();
            itemResponseDto.toItemResponseDto(item);
            itemDtoList.add(itemResponseDto);
        }

        storeDetailDto.setItemDtoList(itemDtoList);

        return storeDetailDto;
    }

}
