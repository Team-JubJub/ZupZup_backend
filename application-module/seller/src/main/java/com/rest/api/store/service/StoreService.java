package com.rest.api.store.service;

import domain.auth.Seller.Seller;
import domain.store.Store;
import dto.auth.seller.test.SellerTestSignInDto;
import dto.auth.seller.test.TestSignInResponseDto;
import dto.store.seller.request.PatchDto;
import dto.store.seller.response.GetStoreDetailsDto;
import dto.store.seller.response.Response;
import exception.store.seller.NoSuchStoreException;
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
    public Response modifyStore(Long storeId, PatchDto patchDto, MultipartFile storeImg) throws IOException {
        Store store = isStorePresent(storeId);

        if(storeImg != null) {
            String imageURL = s3Uploader.upload(storeImg, store.getStoreName());
            patchDto.setStoreImageUrl(imageURL);
        } else {
            patchDto.setStoreImageUrl(""); // 이미지가 없을시 null 로 설정
        }

        store.modifyStore(patchDto);

        Response response = modelMapper.map(store, Response.class);

        return response;
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
