package com.rest.api.store.service;

import com.rest.api.utils.AuthUtils;
import domain.auth.User.User;
import domain.item.Item;
import domain.order.Order;
import domain.store.Store;
import dto.item.customer.response.ItemResponseDto;
import dto.store.customer.response.GetStoreDetailsDto;
import dto.store.customer.response.GetStoreDto;
import exception.NoSuchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ItemRepository;
import repository.StoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
    private final AuthUtils authUtils;

    // <-------------------- GET part -------------------->
    public List<GetStoreDetailsDto> storeListByCategory(String category) {   // 현재는 예외처리할 것 없어 보임
        List<Store> allStoreEntityByCategoryList = storeRepository.findByCategory(category); // 나중에는 위치기반 등으로 거르게 될 듯?
        List<GetStoreDetailsDto> allStoreDtoByCategoryList = allStoreEntityByCategoryList.stream()
                .map(m -> {
                    GetStoreDetailsDto getStoreDetailsDto = modelMapper.map(m, GetStoreDetailsDto.class);
                    getStoreDetailsDto.setStarredUserCount(m.getStarredUsers().size());
                    return getStoreDetailsDto;
                })
                .collect(Collectors.toList());

        return allStoreDtoByCategoryList;
    }

    public List<GetStoreDetailsDto> storeList() {   // 현재는 예외처리할 것 없어 보임
        List<Store> allStoreEntityList = storeRepository.findAll(); // 나중에는 위치기반 등으로 거르게 될 듯?
        List<GetStoreDetailsDto> allStoreDetailsDtoList = allStoreEntityList.stream()
                .map(m -> {
                    GetStoreDetailsDto getStoreDetailsDto = modelMapper.map(m, GetStoreDetailsDto.class);
                    getStoreDetailsDto.setStarredUserCount(m.getStarredUsers().size());
                    return getStoreDetailsDto;
                })
                .collect(Collectors.toList());

        return allStoreDetailsDtoList;
    }

    public List<GetStoreDetailsDto> starredStoreList(String accessToken) {
        User userEntity = authUtils.getUserEntity(accessToken);
        List<Long> starredStores = userEntity.getStarredStores();
        List<GetStoreDetailsDto> allStoreDtoByStarredList = new ArrayList<>();
        for (int i = 0; i < starredStores.size(); i++) {    // 찜목록 돌며 아이디로 db에서 조회, list에 add
            Long starredStoreId = starredStores.get(i);
            Store storeEntity = storeRepository.findById(starredStoreId).get();
            allStoreDtoByStarredList.add(modelMapper.map(storeEntity, GetStoreDetailsDto.class));
            allStoreDtoByStarredList.get(i).setStarredUserCount(storeEntity.getStarredUsers().size());  // 찜한 유저 수 추가
        }

        return allStoreDtoByStarredList;
    }

    public List<GetStoreDetailsDto> searchedStoreList(String storeName) {  // 검색 함수인데, 혹시 몰라서 놔둠.
        List<Store> searchedStoreEntityList = storeRepository.findByStoreNameContaining(storeName);
        List<GetStoreDetailsDto> searchedStoreDtoList = searchedStoreEntityList.stream()
                .map(m -> modelMapper.map(m, GetStoreDetailsDto.class))
                .collect(Collectors.toList());

        return searchedStoreDtoList;
    }

    public GetStoreDetailsDto storeDetails(Long storeId) {

        //store entity 가져와서 DTO로 변환
        Store storeEntity = isStorePresent(storeId);
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

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Store isStorePresent(Long storeId) {
        try {
            Store storeEntity = storeRepository.findById(storeId).get();
            return storeEntity;
        }   catch (NoSuchElementException e) {
            throw new NoSuchException("해당 가게를 찾을 수 없습니다.");
        }
    }

}
