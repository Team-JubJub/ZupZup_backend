package com.rest.api.auth.service;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.redis.RedisService;
import domain.auth.Role;
import domain.auth.Seller.Seller;
import domain.store.Store;
import dto.MessageDto;
import dto.auth.seller.SellerDto;
import dto.auth.seller.request.SellerSignInDto;
import dto.auth.seller.test.SellerTestSignUpDto;
import dto.auth.token.seller.SellerTokenInfoDto;
import dto.store.StoreDto;
import exception.auth.seller.NoSellerPresentsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.SellerRepository;
import repository.StoreRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileAuthService {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final SellerRepository sellerRepository;
    private final StoreRepository storeRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    final static public String LOGIN_FAILS = "Login fails";
    final static public String LOGIN_SUCCESS = "Login success";

    // <-------------------- Sign-in part -------------------->
    public SellerTokenInfoDto signInWithSellerLoginId(SellerSignInDto sellerSignInDto) {
        String sellerLoginId = sellerSignInDto.getLoginId();
        String sellerLoginPwd = sellerSignInDto.getLoginPwd();

        SellerTokenInfoDto sellerTokenInfoDto = new SellerTokenInfoDto();
        Seller sellerEntity = sellerRepository.findSellerByLoginId(sellerLoginId);
        if (sellerEntity == null) throw new NoSellerPresentsException();    // 로그인 아이디가 잘못됐을 경우
        if (!isValidPassword(sellerEntity, sellerLoginPwd)) {   // 비밀번호가 잘못됐을 경우
            sellerTokenInfoDto.setResult(LOGIN_FAILS);
            sellerTokenInfoDto.setMessage("Wrong password");
            sellerTokenInfoDto.setAccessToken(null);
            sellerTokenInfoDto.setRefreshToken(null);

            return sellerTokenInfoDto;
        }
        SellerDto sellerDto = modelMapper.map(sellerEntity, SellerDto.class);
        updateStoreDeviceTokens(sellerSignInDto.getDeviceToken(), sellerEntity, "add");

        sellerTokenInfoDto = generateTokens(sellerDto, "Token generated");

        return sellerTokenInfoDto;
    }

    // < -------------- Sign-out part -------------- >
    public String signOut(String accessToken, String refreshToken, String deviceToken) {
        String sellerLoginId = jwtTokenProvider.getLoginId(accessToken);
        Seller sellerEntity = sellerRepository.findSellerByLoginId(sellerLoginId);
        updateStoreDeviceTokens(deviceToken, sellerEntity, "remove");

        Long remainExpiration = jwtTokenProvider.remainExpiration(accessToken); // 남은 expiration을 계산함.
        if (remainExpiration >= 1) {
            redisService.deleteKey(refreshToken); // refreshToken을 key로 하는 데이터 redis에서 삭제
            redisService.setStringValue(accessToken, "sign-out", remainExpiration); // access token 저장(key: acc_token, value: "sign-out")
            return "success";
        }

        return "fail";
    }

    // <-------------------- Account recovery part -------------------->
//    public String accountRecovery(UserRequestDto.AccountRecoveryDto accountRecoveryDto) { // 아직 어떻게 구현될지 모름
//        String phoneNumber = accountRecoveryDto.getPhoneNumber();
//        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
//        if(userEntity.isEmpty()) {  // 가입된 회원이 없으면
//            return NO_USER_FOUND;
//        }
//        String provider = userEntity.get().getProvider().getProvider();
//
//        return provider;
//    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for readability --->
    private boolean isValidPassword(Seller sellerEntity, String loginPwd) {
        if (!passwordEncoder.matches(loginPwd, sellerEntity.getLoginPwd())) return false;

        return true;
    }

    private void updateStoreDeviceTokens(String deviceToken, Seller sellerEntity, String mode) {
        Store store = storeRepository.findBySellerId(sellerEntity.getSellerId());   // device token update 시작
        StoreDto storeDto = modelMapper.map(store, StoreDto.class);
        if (mode.equals("add")) storeDto.getDeviceTokens().add(deviceToken); // 해당 device token add
        else if (mode.equals("remove")) storeDto.getDeviceTokens().remove(String.valueOf(deviceToken)); // 해당 device token remove
        store.updateDeviceTokens(storeDto);
        storeRepository.save(store);    // device token update 종료
    }

    private SellerTokenInfoDto generateTokens(SellerDto sellerDto, String message) {
        Store storeEntity = storeRepository.findBySellerId(sellerDto.getSellerId());
        Long storeId = storeEntity.getStoreId();
        List<String> roles = Arrays.asList(sellerDto.getRole().getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(sellerDto.getLoginId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        redisService.setStringValue(refreshToken, sellerDto.getLoginId(), JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        SellerTokenInfoDto sellerTokenInfoDto = new SellerTokenInfoDto(LOGIN_SUCCESS, message, accessToken, refreshToken, storeId);

        return sellerTokenInfoDto;
    }


    // < ---------- Test part ---------- >
    public Seller testSignUp(SellerTestSignUpDto sellerTestSignUpDto) {
        Seller sellerEntity = Seller.SellerBuilder()
                .loginId(sellerTestSignUpDto.getLoginId())
                .loginPwd(passwordEncoder.encode(sellerTestSignUpDto.getLoginPwd()))
                .name(sellerTestSignUpDto.getName())
                .phoneNumber(sellerTestSignUpDto.getPhoneNumber())
                .email(sellerTestSignUpDto.getEmail())
                .role(Role.ROLE_SELLER)
                .build();
        sellerRepository.save(sellerEntity);

        return sellerEntity;
    }

}
