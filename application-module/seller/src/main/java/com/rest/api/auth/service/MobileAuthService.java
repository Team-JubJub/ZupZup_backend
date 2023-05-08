package com.rest.api.auth.service;

import domain.order.Order;
import domain.store.Store;
import dto.auth.seller.request.AuthRequestDto;
import dto.auth.seller.response.AuthResponseDto;
import exception.NoSuchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.StoreRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileAuthService {

    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;

    // <-------------------- Sign-in part -------------------->
    public AuthResponseDto.SignInResponseDto signIn(AuthRequestDto.SellerSignInDto sellerSignInDto) {   // 현재 로그인은 파이어베이스 스토어 값 리턴하게
        String loginId = sellerSignInDto.getLoginId();
        String loginPwd = sellerSignInDto.getLoginPwd();
        AuthResponseDto.SignInResponseDto signInResponseDto = new AuthResponseDto.SignInResponseDto();
        Store storeEntity = isStorePresent(loginId);

        if (!isValidPassword(storeEntity, loginPwd)) {   // 비밀번호 검증 실패 시
            signInResponseDto.setMessage("Login fails");
            signInResponseDto.setFireBaseStoreId(Long.valueOf(-1)); // 실패 시 id -1 리턴
            return signInResponseDto;
        }

        Long fireBaseStoreId = storeEntity.getFireBaseStoreId();
        signInResponseDto.setMessage("Login success");   // 임시임. Dto 필드 유형 String -> Long으로 수정 등 구조 수정 할 것.
        signInResponseDto.setFireBaseStoreId(fireBaseStoreId);

        return signInResponseDto;
    }

    private boolean isValidPassword(Store storeEntity, String loginPwd) {
        String encodedPwd = passwordEncoder.encode(loginPwd);
        if (!storeEntity.getLoginPwd().equals(encodedPwd)) return false;    // 비밀번호가 다르면 false 리턴

        return true;
    }


    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Store isStorePresent(String loginId) {
        try {
            Store storeEntity = storeRepository.findByLoginId(loginId);
            return storeEntity;
        }   catch (NoSuchElementException e) {
            throw new NoSuchException("No user found");
        }
    }


    // <-------------------- Methods for test -------------------->


}
