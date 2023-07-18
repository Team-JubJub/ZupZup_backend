package com.rest.api.auth.service;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.redis.RedisService;
import domain.auth.Seller.Seller;
import domain.auth.User.User;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.UserRequestDto;
import dto.auth.seller.SellerDto;
import dto.auth.seller.request.SellerRequestDto;
import dto.auth.token.TokenInfoDto;
import exception.customer.NoUserPresentsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.SellerRepository;
import repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileAuthService {

    @Autowired
    ModelMapper modelMapper;
    private final SellerRepository sellerRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    final static public String NO_USER_FOUND = "No user found";
    // <-------------------- Sign-in part -------------------->
    public TokenInfoDto signInWithSellerUserId(SellerRequestDto.SellerSignInDto sellerSignInDto) {
        String sellerLoginId = sellerSignInDto.getLoginId();
        TokenInfoDto tokenInfoDto = new TokenInfoDto();
        try {
            Seller sellerEntity = sellerRepository.findSellerByLoginId(sellerLoginId);
            SellerDto sellerDto = modelMapper.map(sellerEntity, SellerDto.class);
            tokenInfoDto = generateTokens(sellerDto, "Token refreshed");
        } catch (NoSuchElementException e) {
            throw new NoUserPresentsException();
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
    private TokenInfoDto generateTokens(SellerDto sellerDto, String message) {
        List<String> roles = Arrays.asList(sellerDto.getRole().getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(sellerDto.getLoginId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        redisService.setStringValue(refreshToken, sellerDto.getLoginId(), JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        TokenInfoDto tokenInfoDto = new TokenInfoDto("success", message, accessToken, refreshToken);

        return tokenInfoDto;
    }

}
