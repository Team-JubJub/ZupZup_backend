package com.rest.api.store.service;

import com.zupzup.untact.domain.auth.Seller.Seller;
import com.zupzup.untact.domain.store.Store;
import com.zupzup.untact.dto.auth.seller.test.SellerTestSignInDto;
import com.zupzup.untact.dto.auth.seller.test.TestSignInResponseDto;
import com.zupzup.untact.dto.store.seller.request.ModifyStoreDto;
import com.zupzup.untact.dto.store.seller.response.GetStoreDetailsDto;
import com.zupzup.untact.dto.store.seller.response.ModifyStoreResponse;
import com.zupzup.untact.repository.SellerRepository;
import com.zupzup.untact.repository.StoreRepository;
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
    private final SellerRepository sellerRepository;
    private final StoreRepository storeRepository;
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

        return "공지사항이 수정되었습니다.";
    }

    //For Test
    public TestSignInResponseDto testSignIn(SellerTestSignInDto sellerTestSignInDto) {
        String loginId = sellerTestSignInDto.getLoginId();
        String loginPwd = sellerTestSignInDto.getLoginPwd();
        Seller seller = sellerRepository.findSellerByLoginId(loginId);
        Long sellerId = seller.getSellerId();
        Store store = storeRepository.findBySellerId(sellerId);
        TestSignInResponseDto testSignInResponseDto = new TestSignInResponseDto();
        testSignInResponseDto.setMessage("success");
        testSignInResponseDto.setStoreId(store.getStoreId());

        if (loginPwd.equals(seller.getLoginPwd())) {
            return testSignInResponseDto;
        }

        return new TestSignInResponseDto();
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Store isStorePresent(Long storeId) {
        try {
            Store store = storeRepository.findById(storeId).get();
            return store;
        } catch (NoSuchElementException e) {
            throw new NoSuchStoreException("해당 가게를 찾을 수 없습니다.");
        }
    }

}
