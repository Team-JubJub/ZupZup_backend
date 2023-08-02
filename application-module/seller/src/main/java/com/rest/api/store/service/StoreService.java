package com.rest.api.store.service;

import domain.auth.Seller.Seller;
import domain.store.Store;
import dto.auth.seller.request.SellerTestSignInDto;
import dto.auth.seller.response.SellerResponseDto;
import dto.store.seller.request.StoreRequestDto;
import dto.store.seller.response.StoreResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import repository.SellerRepository;
import repository.StoreRepository;

import java.io.IOException;

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
    public StoreResponseDto.GetStoreDetailsDto storeDetails(Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        StoreResponseDto.GetStoreDetailsDto getStoreDetailsDto = modelMapper.map(store, StoreResponseDto.GetStoreDetailsDto.class);

        return getStoreDetailsDto;
    }

    // 가게 영업중 여부 전환
    public String changeIsOpened(Long storeId, Boolean isOpened) {

        Store store = storeRepository.findById(storeId).get();
        store.setIsOpen(isOpened);

        if(isOpened) {
            return "영업중입니다.";
        }
        return "휴무일입니다.";
    }

    // 가게 영업시간, 할인시간, 휴무일, 이미지 변경
    public StoreResponseDto.response modifyStore(Long storeId, StoreRequestDto.patchDto patchDto, MultipartFile storeImg) throws IOException {

        Store store = storeRepository.findById(storeId).get();

        if(storeImg != null) {
            String imageURL = s3Uploader.upload(storeImg, store.getStoreName());
            patchDto.setStoreImageUrl(imageURL);
        } else {
            patchDto.setStoreImageUrl(""); // 이미지가 없을시 null 로 설정
        }

        store.modifyStore(patchDto);

        StoreResponseDto.response response = modelMapper.map(store, StoreResponseDto.response.class);

        return response;
    }

    // 공지사항 수정
    public String changeNotification(Long storeId, String storeMatters) {

        Store store = storeRepository.findById(storeId).get();
        store.setSaleMatters(storeMatters);

        return "공지사항이 수정되었습니다.";
    }

    //For Test
    public SellerResponseDto.TestSignInResponseDto testSignIn(SellerTestSignInDto sellerTestSignInDto) {
        String loginId = sellerTestSignInDto.getLoginId();
        String loginPwd = sellerTestSignInDto.getLoginPwd();
        Seller seller = sellerRepository.findSellerByLoginId(loginId);
        Long sellerId = seller.getSellerId();
        Store store = storeRepository.findBySellerId(sellerId);
        SellerResponseDto.TestSignInResponseDto testSignInResponseDto = new SellerResponseDto().new TestSignInResponseDto();
        testSignInResponseDto.setMessage("success");
        testSignInResponseDto.setStoreId(store.getStoreId());

        if (loginPwd.equals(seller.getLoginPwd())) {
            return testSignInResponseDto;
        }

        return new SellerResponseDto().new TestSignInResponseDto();
    }

}
