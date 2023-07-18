package com.rest.api.auth.service;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.redis.RedisService;
import domain.auth.User.User;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.UserRequestDto;
import dto.auth.token.TokenInfoDto;
import exception.customer.NoUserPresentsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    final static public String NO_USER_FOUND = "No user found";
    // <-------------------- Sign-in part -------------------->
    public TokenInfoDto signInWithSellerUserId(String provider, UserRequestDto.UserSignInDto userSignInDto) {
        String userUniqueId = userSignInDto.getUserUniqueId();
        TokenInfoDto tokenInfoDto = new TokenInfoDto();
        try {
            User userEntity = userRepository.findByProviderUserId(provider.toUpperCase() + "_" + userUniqueId).get();
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            tokenInfoDto = generateTokens(userDto, "Token refreshed");
        } catch (NoSuchElementException e) {
            throw new NoUserPresentsException();
        }

        return tokenInfoDto;
    }

    // <-------------------- Account recovery part -------------------->
    public String accountRecovery(UserRequestDto.AccountRecoveryDto accountRecoveryDto) {
        String phoneNumber = accountRecoveryDto.getPhoneNumber();
        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if(userEntity.isEmpty()) {  // 가입된 회원이 없으면
            return NO_USER_FOUND;
        }
        String provider = userEntity.get().getProvider().getProvider();

        return provider;
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for readability --->
    private TokenInfoDto generateTokens(UserDto userDto, String message) {
        List<String> roles = Arrays.asList(userDto.getRole().getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(userDto.getProviderUserId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        redisService.setStringValue(refreshToken, userDto.getProviderUserId(), JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        TokenInfoDto tokenInfoDto = new TokenInfoDto("success", message, accessToken, refreshToken);

        return tokenInfoDto;
    }

}
