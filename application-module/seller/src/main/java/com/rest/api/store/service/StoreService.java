package com.rest.api.store.service;

import com.rest.api.utils.FCMUtils;
import com.zupzup.untact.model.domain.enums.EnterState;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.model.dto.store.seller.request.ModifyStoreDto;
import com.zupzup.untact.model.dto.store.seller.response.GetStoreDetailsDto;
import com.zupzup.untact.model.dto.store.seller.response.ModifyStoreResponse;
import com.zupzup.untact.repository.StoreRepository;
import exception.store.ForbiddenStoreException;
import exception.store.seller.NoSuchStoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
@Log
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final S3Uploader s3Uploader;
    private final StoreRepository storeRepository;
    private final FCMUtils fcmUtils;
    @Autowired
    ModelMapper modelMapper;

    // 가게 메인 페이지
    public GetStoreDetailsDto storeDetails(Long storeId) {
        Store store = isStorePresent(storeId);

        GetStoreDetailsDto getStoreDetailsDto = modelMapper.map(store, GetStoreDetailsDto.class);

        return getStoreDetailsDto;
    }

    // 가게 영업중 여부 전환
    public String changeIsOpened(Long storeId, Boolean isOpened) {
        Store store = isStorePresent(storeId);
        store.setIsOpen(isOpened);

        if(isOpened) {
            return "영업중입니다.";
        }
        return "휴무일입니다.";
    }

    // 가게 영업시간, 할인시간, 휴무일, 이미지 변경
    public ModifyStoreResponse modifyStore(Long storeId, ModifyStoreDto modifyStoreDto, MultipartFile storeImg) throws IOException {
        Store store = isStorePresent(storeId);

        if(storeImg != null) {
            String imageURL = s3Uploader.upload(storeImg, store.getStoreName());
            modifyStoreDto.setStoreImageUrl(imageURL);
        } else {    // 변경할 이미지를 보내지 않았을 때
            modifyStoreDto.setStoreImageUrl(store.getStoreImageUrl());    // 기존의 이미지를 사용하도록 수정
        }

        store.modifyStore(modifyStoreDto);
        storeRepository.save(store);

        ModifyStoreResponse modifyStoreResponse = modelMapper.map(store, ModifyStoreResponse.class);

        return modifyStoreResponse;
    }

    // 공지사항 수정
    public String changeNotification(Long storeId, String storeMatters) {
        Store store = isStorePresent(storeId);
        store.setSaleMatters(storeMatters);
        fcmUtils.sendMessageToAlertUsers(storeId, "신규 공지 알림", store.getStoreName() + "에 신규 공지가 등록되었어요!");

        return "공지사항이 수정되었습니다.";
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Store isStorePresent(Long storeId) {
        try {
            Store store = storeRepository.findById(storeId).get();
            if (store.getEnterState().equals(EnterState.NEW)) throw new ForbiddenStoreException("해당 가게는 아직 승인 대기중입니다. 관리자에게 연락해주세요.");
            return store;
        } catch (NoSuchElementException e) {
            throw new NoSuchStoreException("해당 가게를 찾을 수 없습니다.");
        }
    }

}
