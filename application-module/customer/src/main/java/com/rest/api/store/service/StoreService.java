package com.rest.api.store.service;

import domain.item.Item;
import domain.store.Store;
import dto.item.customer.response.ItemResponseDto;
import dto.store.customer.response.GetStoreDetailsDto;
import dto.store.customer.response.GetStoreDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ItemRepository;
import repository.StoreRepository;

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
        List<Store> allStoreEntityList = storeRepository.findAll(); // 나중에는 위치기반 등으로 거르게 될 듯?
        List<GetStoreDto> allStoreDtoList = allStoreEntityList.stream()
                .map(m -> {
                    GetStoreDto getStoreDto = modelMapper.map(m, GetStoreDto.class);
                    getStoreDto.setStarredUserCount(m.getStarredUsers().size());
                    return getStoreDto;
                })
                .collect(Collectors.toList());

        return allStoreDtoList;
    }

    public List<GetStoreDto> searchedStoreList(String storeName) {
        List<Store> searchedStoreEntityList = storeRepository.findByStoreNameContaining(storeName);
        List<GetStoreDto> searchedStoreDtoList = searchedStoreEntityList.stream()
                .map(m -> modelMapper.map(m, GetStoreDto.class))
                .collect(Collectors.toList());

        return searchedStoreDtoList;
    }

    public List<GetStoreDto> storeListByCategory(String category) {   // 현재는 예외처리할 것 없어 보임
        List<Store> allStoreEntityByCategoryList = storeRepository.findByCategory(category); // 나중에는 위치기반 등으로 거르게 될 듯?
        List<GetStoreDto> allStoreDtoByCategoryList = allStoreEntityByCategoryList.stream()
                .map(m -> {
                    GetStoreDto getStoreDto = modelMapper.map(m, GetStoreDto.class);
                    getStoreDto.setStarredUserCount(m.getStarredUsers().size());
                    return getStoreDto;
                })
                .collect(Collectors.toList());

        return allStoreDtoByCategoryList;
    }

    public GetStoreDetailsDto storeDetail(Long storeId) {

        //store entity 가져와서 DTO로 변환
        Store storeEntity = storeRepository.findById(storeId).get();
        GetStoreDetailsDto storeDetailsDto = modelMapper.map(storeEntity, GetStoreDetailsDto.class);
        storeDetailsDto.setStarredUserCount(storeEntity.getStarredUsers().size());

        //item list 생성 및 StoreDto에 저장
        List<Item> itemEntityList = itemRepository.findAllByStore(storeEntity);
        List<ItemResponseDto> itemDtoList = itemEntityList.stream()
                .map(m -> modelMapper.map(m, ItemResponseDto.class))
                .collect(Collectors.toList());
        storeDetailsDto.setItemDtoList(itemDtoList);

        return storeDetailsDto;
    }

}
