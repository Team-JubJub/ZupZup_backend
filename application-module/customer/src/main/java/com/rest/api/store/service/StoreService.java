package com.rest.api.store.service;

import com.rest.api.utils.AuthUtils;
import domain.auth.User.User;
import domain.item.Item;
import domain.order.Order;
import domain.store.Store;
import domain.store.type.EnterState;
import domain.store.type.StoreCategory;
import dto.item.customer.response.ItemResponseDto;
import dto.store.customer.response.GetStoreDetailsDto;
import dto.store.customer.response.GetStoreDto;
import exception.NoSuchException;
import exception.store.ForbiddenStoreException;
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
        StoreCategory storeCategory = StoreCategory.valueOf(category.toUpperCase());
        List<Store> allStoreEntityByCategoryList = storeRepository.findByCategory(storeCategory);

        List<GetStoreDetailsDto> allStoreDtoByCategoryList = allStoreEntityByCategoryList.stream()
                .filter(m -> m.getEnterState().equals(EnterState.CONFIRM))  // EnterState가 CONFIRM이 아닌 것은 거름.
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
                .filter(m -> m.getEnterState().equals(EnterState.CONFIRM))  // EnterState가 CONFIRM이 아닌 것은 거름.
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
            if (!storeEntity.getEnterState().equals(EnterState.CONFIRM)) continue;  // CONFIRM 상태가 아닌 가게는 보여줄 리스트에 추가하지 않음.(찜은 이미 CONFIRM 상태이겠지만, 후에 WAIT 상태로 되돌릴 수도 있는 것을 고려해 추가해놓음)
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

    public GetStoreDetailsDto storeDetails(String accessToken, Long storeId) {
        User userEntity = authUtils.getUserEntity(accessToken);

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

        if (userEntity.getStarredStores().contains(storeId)) storeDetailsDto.setIsStarred(true);    // 찜설정되었는지 여부 체크
        else storeDetailsDto.setIsStarred(false);
        if (userEntity.getAlertStores().contains(storeId)) storeDetailsDto.setIsAlerted(true);  // 알림설정되었는지 여부 체크
        else storeDetailsDto.setIsAlerted(false);

        return storeDetailsDto;
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Store isStorePresent(Long storeId) {
        try {
            Store storeEntity = storeRepository.findById(storeId).get();
            if (!storeEntity.getEnterState().equals(EnterState.CONFIRM)) throw new ForbiddenStoreException("사용자의 접근이 승인되지 않은 가게입니다.");    // CONFIRM 상태인 가게가 아니면 조회 불가(가게 리스트 반환에서 안보이게 처리하지만, 혹시 모를 접근을 한 번 더 막는 용도)
            return storeEntity;
        }   catch (NoSuchElementException e) {
            throw new NoSuchException("해당 가게를 찾을 수 없습니다.");
        }
    }

}
