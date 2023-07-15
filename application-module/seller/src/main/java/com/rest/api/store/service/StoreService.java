package com.rest.api.store.service;

import domain.auth.Seller.Seller;
import domain.store.Store;
import dto.auth.seller.request.AuthRequestDto;
import dto.auth.seller.response.AuthResponseDto;
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

    // 가게 저장

    // 가게 메인 페이지
    /*public StoreResponseDto mainPage(Long storeId) {

        // 가게 관련 내용 (가게 이름 및 운영 시간, 이벤트 내용, 오늘 할인 시간)
        // 제품 관련 내용 ([제품 이미지, 제품 이름, 가격])

        Store store = storeRepository.findById(storeId).get();
        StoreResponseDto responseDto = new StoreResponseDto();

        // 엔티티->Dto
        responseDto.setStoreId(store.getStoreId());
        responseDto.setStoreName(store.getStoreName());
        responseDto.setOpenTime(store.getOpenTime());
        responseDto.setEndTime(store.getCloseTime());
        responseDto.setSaleMatters(store.getSaleMatters());
        responseDto.setSaleTimeStart(store.getSaleTimeStart());
        responseDto.setSaleTimeEnd(store.getSaleTimeEnd());

        // 아이템 가져오기 및 저장
        List<Item> itemList = itemRepository.findAllByStore(store);
        List<ItemResponseDto> itemDtoList = new ArrayList<>();

        for(Item item : itemList) {

            ItemResponseDto itemDto = new ItemResponseDto();
            itemDtoList.add(itemDto.toItemResponseDto(item));
        }

        responseDto.setStoreItems(itemDtoList);

        return responseDto;
    }*/

    // 가게 영업중 여부 전환
    public String changeIsOpened(Long storeId, Boolean isOpened) {

        Store store = storeRepository.findById(storeId).get();
        store.setIsOpen(isOpened);

        if(isOpened) {
            return "영업중입니다.";
        }
        return "휴무일입니다.";
    }

    // 가게 영업시간, 할인시간, 이미지 변경
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
    public AuthResponseDto.TestSignInResponseDto testSignIn(AuthRequestDto.SellerSignInDto sellerSignInDto) {
        String loginId = sellerSignInDto.getLoginId();
        String loginPwd = sellerSignInDto.getLoginPwd();
        Seller seller = sellerRepository.findSellerByLoginId(loginId);
        Long sellerId = seller.getSellerId();
        Store store = storeRepository.findBySellerId(sellerId);
        AuthResponseDto.TestSignInResponseDto testSignInResponseDto = new AuthResponseDto.TestSignInResponseDto();
        testSignInResponseDto.setMessage("success");
        testSignInResponseDto.setStoreId(store.getStoreId());

        if (loginPwd.equals(seller.getLoginPwd())) {
            return testSignInResponseDto;
        }

        return new AuthResponseDto.TestSignInResponseDto();
    }

}
