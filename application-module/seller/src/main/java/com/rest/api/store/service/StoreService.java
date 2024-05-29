package com.rest.api.store.service;

import com.rest.api.aws.S3Uploader;
import com.rest.api.utils.FCMUtils;
import com.zupzup.untact.exception.store.StoreException;
import com.zupzup.untact.model.enums.EnterState;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.dto.store.seller.request.ModifyStoreDto;
import com.zupzup.untact.dto.store.seller.response.GetStoreDetailsDto;
import com.zupzup.untact.dto.store.seller.response.ModifyStoreResponse;
import com.zupzup.untact.repository.StoreRepository;
import com.zupzup.untact.exception.store.ForbiddenStoreException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.zupzup.untact.exception.store.StoreExceptionType.NO_MATCH_STORE;
import static com.zupzup.untact.exception.store.StoreExceptionType.SEVER_ERR;

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

        return modelMapper.map(store, GetStoreDetailsDto.class);
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

        return modelMapper.map(store, ModifyStoreResponse.class);
    }

    // 공지사항 수정
    public String changeNotification(Long storeId, String storeMatters) {
        Store store = isStorePresent(storeId);
        store.setSaleMatters(storeMatters);
        fcmUtils.sendMessageToAlertUsers(storeId, "신규 공지 알림", store.getStoreName() + "에 신규 공지가 등록되었어요!");

        return "공지사항이 수정되었습니다.";
    }

    // 리뷰 관련 공지사항 작성
//    public String setReviewAnnouncement(ReviewAnnouncementRequest reviewAnnouncementRequest,
//                                        Long storeId) {
//
//        Store store = isStorePresent(storeId);
//
//        // 리뷰 공지사항 저장
//        store.setReviewAnnouncement(reviewAnnouncementRequest.getReviewAnnouncement());
//        storeRepository.save(store);
//
//        // 처음 저장 시에 저장하지 않기 때문에 저장과 수정을 같은 메소드로 사용
//        return "리뷰 공지사항이 작성(수정) 되었습니다.";
//    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Store isStorePresent(Long storeId) {
        try {
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new StoreException(NO_MATCH_STORE));
            if (store.getEnterState().equals(EnterState.NEW)) throw new ForbiddenStoreException("해당 가게는 아직 승인 대기중입니다. 관리자에게 연락해주세요.");
            return store;
        } catch (Exception e) {

            // 위에 발생하는 에러 외의 에러 발생 시 서버 에러로 처리
            throw new StoreException(SEVER_ERR);
        }
    }

}
