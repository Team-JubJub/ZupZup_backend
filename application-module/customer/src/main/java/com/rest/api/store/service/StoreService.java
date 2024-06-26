package com.rest.api.store.service;

import com.zupzup.untact.social.utils.AuthUtils;
import com.zupzup.untact.model.domain.auth.user.User;
import com.zupzup.untact.model.enums.EnterState;
import com.zupzup.untact.model.enums.StoreCategory;
import com.zupzup.untact.model.domain.item.Item;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.dto.auth.customer.UserDto;
import com.zupzup.untact.dto.item.customer.response.ItemResponseDto;
import com.zupzup.untact.dto.store.StoreDto;
import com.zupzup.untact.dto.store.customer.response.GetStoreDetailsDto;
import com.zupzup.untact.repository.ItemRepository;
import com.zupzup.untact.repository.StoreRepository;
import com.zupzup.untact.repository.UserRepository;
import com.zupzup.untact.exception.store.order.NoSuchException;
import com.zupzup.untact.exception.store.ForbiddenStoreException;
import com.zupzup.untact.exception.store.customer.StoreNotStarredException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class  StoreService {

    @Autowired
    ModelMapper modelMapper;
    private final UserRepository userRepository;
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
                    setItemDtoList(m, getStoreDetailsDto);
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
                    setItemDtoList(m, getStoreDetailsDto);
                    return getStoreDetailsDto;
                })
                .collect(Collectors.toList());

        return allStoreDetailsDtoList;
    }

    public List<GetStoreDetailsDto> starredStoreList(String accessToken) {
        User userEntity = authUtils.getUserEntity(accessToken);
        List<Long> starredStores = new ArrayList<>(userEntity.getStarredStores());
        List<GetStoreDetailsDto> allStoreDtoByStarredList = new ArrayList<>();
        for (int i = 0; i < starredStores.size(); i++) {    // 찜목록 돌며 아이디로 db에서 조회, list에 add
            Long starredStoreId = starredStores.get(i);
            Store storeEntity = storeRepository.findById(starredStoreId).get();
            if (!storeEntity.getEnterState().equals(EnterState.CONFIRM)) continue;  // CONFIRM 상태가 아닌 가게는 보여줄 리스트에 추가하지 않음.(찜은 이미 CONFIRM 상태이겠지만, 후에 WAIT 상태로 되돌릴 수도 있는 것을 고려해 추가해놓음)
            GetStoreDetailsDto storeDetailsDto = modelMapper.map(storeEntity, GetStoreDetailsDto.class);
            storeDetailsDto.setStarredUserCount(storeEntity.getStarredUsers().size());  // 찜한 유저 수 추가
            setItemDtoList(storeEntity, storeDetailsDto);   // item 추가
            allStoreDtoByStarredList.add(storeDetailsDto);
        }

        return allStoreDtoByStarredList;
    }

    public List<GetStoreDetailsDto> searchedStoreList(String storeName) {  // 검색 함수인데, 혹시 몰라서 놔둠.
        List<Store> searchedStoreEntityList = storeRepository.findByStoreNameContaining(storeName);
        List<GetStoreDetailsDto> searchedStoreDtoList = searchedStoreEntityList.stream()
                .map(m -> {
                    GetStoreDetailsDto getStoreDetailsDto = modelMapper.map(m, GetStoreDetailsDto.class);
                    getStoreDetailsDto.setStarredUserCount(m.getStarredUsers().size());
                    setItemDtoList(m, getStoreDetailsDto);
                    return getStoreDetailsDto;
                })
                .collect(Collectors.toList());

        return searchedStoreDtoList;
    }

    public GetStoreDetailsDto storeDetails(String accessToken, Long storeId) {
        User userEntity = authUtils.getUserEntity(accessToken);

        //store entity 가져와서 DTO로 변환
        Store storeEntity = isStorePresent(storeId);
        GetStoreDetailsDto storeDetailsDto = modelMapper.map(storeEntity, GetStoreDetailsDto.class);
        storeDetailsDto.setStarredUserCount(storeEntity.getStarredUsers().size());
        setItemDtoList(storeEntity, storeDetailsDto);

        if (userEntity.getStarredStores().contains(storeId)) storeDetailsDto.setIsStarred(true);    // 찜설정되었는지 여부 체크
        else storeDetailsDto.setIsStarred(false);
        if (userEntity.getAlertStores().contains(storeId)) storeDetailsDto.setIsAlerted(true);  // 알림설정되었는지 여부 체크
        else storeDetailsDto.setIsAlerted(false);

        return storeDetailsDto;
    }

    // <-------------------- PATCH part -------------------->
    public boolean modifyStarStore(String accessToken, Long storeId, String action) {
        boolean result = false;
        User userEntity = authUtils.getUserEntity(accessToken);
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        Store storeEntity = isStorePresent(storeId);
        StoreDto storeDto = modelMapper.map(storeEntity, StoreDto.class);

        Set<Long> starredStoreList = userDto.getStarredStores();   // 가져오고
        Set<Long> starredUserList = storeDto.getStarredUsers();
        if (action.equals("set")) { // 더해주고
            if (starredStoreList == null) starredStoreList = new HashSet<>(); // 값이 null이라면 새 list로 초기화
            if (starredUserList == null) starredUserList = new HashSet<>();
            starredStoreList.add(storeId);
            starredUserList.add(userEntity.getId());

            result = true;
        }
        else if (action.equals("unset")) {  // 빼주고
            if (isStoreAlerted(userEntity, storeId)) {  // 찜 해제 시 알림 설정도 해제해줘야 함
                userDto.setAlertStores(removeAlertElement(userDto.getAlertStores(), storeId));
                storeDto.setAlertUsers(removeAlertElement(storeDto.getAlertUsers(), userEntity.getId()));
            }
            starredStoreList.remove(Long.valueOf(storeId));
            starredUserList.remove(Long.valueOf(userEntity.getId()));

            result = false;
        }
        userDto.setStarredStores(starredStoreList); // 바꿔주고
        storeDto.setStarredUsers(starredUserList);
        userEntity.updateStarredStoreList(userDto); // 저장
        storeEntity.updateStarredUserList(storeDto);

        userRepository.save(userEntity);    // 최종 처리 후 db에 저장
        storeRepository.save(storeEntity);

        return result;
    }

    public boolean modifyAlertStore(String accessToken, Long storeId, String action) {
        boolean result = false;
        User userEntity = authUtils.getUserEntity(accessToken);
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        Store storeEntity = isStorePresent(storeId);
        StoreDto storeDto = modelMapper.map(storeEntity, StoreDto.class);

        Set<Long> alertStoreList = userDto.getAlertStores();   // 가져오고
        Set<Long> alertUserList = storeDto.getAlertUsers();
        if (action.equals("set")) { // 설정의 경우, 찜했는지 안했는지 따져봐야 함.
            if (!isStoreStarred(userEntity, storeId)) throw new StoreNotStarredException("찜한 가게만 알림을 설정을 할 수 있습니다."); // 예외 던지기

            if (alertStoreList == null) alertStoreList = new HashSet<>(); // 값이 null이라면 새 list로 초기화
            if (alertUserList == null) alertUserList = new HashSet<>();
            alertStoreList.add(storeId);
            alertUserList.add(userEntity.getId());

            result = true;
        } else if (action.equals("unset")) {    // 해제의 경우는 바로 remove 처리 후 끝
            alertStoreList.remove(Long.valueOf(storeId));
            alertUserList.remove(Long.valueOf(userEntity.getId()));

            result = false;
        }
        userDto.setAlertStores(alertStoreList); // 바꿔주고
        storeDto.setAlertUsers(alertUserList);
        userEntity.updateAlertStoreList(userDto); // 저장
        storeEntity.updateAlertUserList(storeDto);

        userRepository.save(userEntity);    // 최종 처리 후 db에 저장
        storeRepository.save(storeEntity);

        return result;
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

    private boolean isStoreStarred(User userEntity, Long storeId) {
        List<Long> starredStores = new ArrayList<>(userEntity.getStarredStores());
        for (int i = 0; i < starredStores.size(); i++) {
            if (starredStores.get(i) == storeId) {  // 찜 목록에 있으면 true 반환
                return true;
            }
        }

        return false;   // 없으면 false 반환
    }

    private boolean isStoreAlerted(User userEntity, Long storeId) { // 찜 해제 시 알림 설정도 같이 끄기 위해, 알림 설정 여부 판단
        List<Long> alertStores = new ArrayList<>(userEntity.getAlertStores());
        for (int i = 0; i < alertStores.size(); i++) {
            if (alertStores.get(i) == storeId) {  // 알림 목록에 있으면 true 반환
                return true;
            }
        }

        return false;
    }

    // <--- Methods for readability --->
    private void setItemDtoList(Store storeEntity, GetStoreDetailsDto storeDetailsDto) {
        //item list 생성 및 StoreDto에 저장
        List<Item> itemEntityList = itemRepository.findAllByStore(storeEntity);
        List<ItemResponseDto> itemDtoList = itemEntityList.stream()
                .map(m -> modelMapper.map(m, ItemResponseDto.class))
                .collect(Collectors.toList());
        storeDetailsDto.setItemDtoList(itemDtoList);
    }

    private Set<Long> removeAlertElement(Set<Long> ogSet, Long id) { // 찜 해제 시 알림 설정도 해제해줄 때의 중복 작업 처리 함수
        Set<Long> retSet = ogSet;
        retSet.remove(Long.valueOf(id));

        return retSet;
    }

}
