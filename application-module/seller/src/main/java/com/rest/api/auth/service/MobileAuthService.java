package com.rest.api.auth.service;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.redis.RedisService;
import domain.auth.Role;
import domain.auth.Seller.Seller;
import dto.auth.seller.SellerDto;
import dto.auth.seller.request.SellerRequestDto;
import dto.auth.token.TokenInfoDto;
import exception.auth.customer.NoUserPresentsException;
import exception.auth.seller.NoSellerPresentsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.SellerRepository;

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
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    final static public String NO_USER_FOUND = "No user found";
    final static public String LOGIN_FAILS = "Login fails";
    final static public String LOGIN_SUCCESS = "Login success";

    // <-------------------- Sign-in part -------------------->
    public TokenInfoDto signInWithSellerLoginId(SellerRequestDto.SellerSignInDto sellerSignInDto) {
        String sellerLoginId = sellerSignInDto.getLoginId();
        String sellerLoginPwd = sellerSignInDto.getLoginPwd();

        TokenInfoDto tokenInfoDto = new TokenInfoDto();
        try {
            Seller sellerEntity = sellerRepository.findSellerByLoginId(sellerLoginId);
            if (!isValidPassword(sellerEntity, sellerLoginPwd)) {   // 비밀번호가 잘못됐을 경우
                tokenInfoDto.setResult(LOGIN_FAILS);
                tokenInfoDto.setResult("Wrong password");
                tokenInfoDto.setAccessToken(null);
                tokenInfoDto.setRefreshToken(null);

                return tokenInfoDto;
            }
            SellerDto sellerDto = modelMapper.map(sellerEntity, SellerDto.class);
            tokenInfoDto = generateTokens(sellerDto, "Token refreshed");
        } catch (NoSuchElementException e) {    // 로그인 id와 매칭되는 사장님 없으면 예외 처리
            throw new NoSellerPresentsException();
        }

        return tokenInfoDto;
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

    private TokenInfoDto generateTokens(SellerDto sellerDto, String message) {
        List<String> roles = Arrays.asList(sellerDto.getRole().getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(sellerDto.getLoginId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        redisService.setStringValue(refreshToken, sellerDto.getLoginId(), JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        TokenInfoDto tokenInfoDto = new TokenInfoDto(LOGIN_SUCCESS, message, accessToken, refreshToken);

        return tokenInfoDto;
    }


    // < ---------- Test part ---------- >
    public Seller testSignUp(SellerRequestDto.SellerTestSignUpDto sellerTestSignUpDto) {
        Seller sellerEntity = Seller.SellerBuilder()
                .loginId(sellerTestSignUpDto.getLoginId())
                .loginPwd(passwordEncoder.encode(sellerTestSignUpDto.getLoginPwd()))
                .role(Role.ROLE_SELLER)
                .build();
        sellerRepository.save(sellerEntity);

        return sellerEntity;
    }

}
