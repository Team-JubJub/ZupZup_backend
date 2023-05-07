package com.rest.api.auth.service;

import domain.store.Store;
import dto.auth.seller.request.AuthRequestDto;
import dto.auth.seller.response.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.StoreRepository;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileAuthService {

    private final StoreRepository storeRepository;

    // <-------------------- Sign-in part -------------------->
    public AuthResponseDto.SignInResponseDto signIn(AuthRequestDto.SellerSignInDto sellerSignInDto) {
        String loginId = sellerSignInDto.getLoginId();
        String loginPwd = sellerSignInDto.getLoginPwd();
        AuthResponseDto.SignInResponseDto signInResponseDto = new AuthResponseDto.SignInResponseDto();
        if (!isValidPassword(loginPwd)) {   // 비밀번호 검증 실패 시
            signInResponseDto.setStoreId("Login fails");
            return signInResponseDto;
        }

        Store storeEntity = storeRepository.findByLoginId(loginId);
        Long storeId = storeEntity.getStoreId();
        signInResponseDto.setStoreId(Long.toString(storeId));   // 임시임. Dto 필드 유형 String -> Long으로 수정 등 구조 수정 할 것.

        return signInResponseDto;
    }

    public boolean isValidPassword(String loginPwd) {

        return true;
    }

}
