package com.rest.api.store.service;

import com.zupzup.untact.domain.auth.Seller.Seller;
import com.zupzup.untact.domain.store.Store;
import com.zupzup.untact.dto.auth.seller.test.SellerTestSignInDto;
import com.zupzup.untact.dto.auth.seller.test.TestSignInResponseDto;
import com.zupzup.untact.dto.store.seller.request.PatchDto;
import com.zupzup.untact.dto.store.seller.response.GetStoreDetailsDto;
import com.zupzup.untact.dto.store.seller.response.Response;
import com.zupzup.untact.repository.SellerRepository;
import com.zupzup.untact.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public GetStoreDetailsDto storeDetails(Long storeId) {
        Store store = storeRepository.findById(storeId).get();
        GetStoreDetailsDto getStoreDetailsDto = modelMapper.map(store, GetStoreDetailsDto.class);

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
    public Response modifyStore(Long storeId, PatchDto patchDto, MultipartFile storeImg) throws IOException {

        Store store = storeRepository.findById(storeId).get();

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

        Store store = storeRepository.findById(storeId).get();
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

}
