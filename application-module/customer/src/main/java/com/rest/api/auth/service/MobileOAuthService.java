package com.rest.api.auth.service;

import com.rest.api.auth.jwt.JwtTokenProvider;

import com.rest.api.auth.redis.RedisService;
import domain.auth.User.Provider;
import domain.auth.User.Role;
import domain.auth.User.User;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.UserRequestDto;
import dto.auth.token.TokenInfoDto;
import exception.customer.AlreadySignedUpException;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileOAuthService {

    @Autowired
    ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    // <-------------------- Sign-up part -------------------->
    public TokenInfoDto signUp(String provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        checkIsSignedUp(userSignUpDto.getPhoneNumber());
        UserDto userDto = new UserDto();
        if((provider.toUpperCase()).equals(Provider.NAVER.getProvider())) {
            userDto = userSignUpDtoToUserDto(Provider.NAVER, userSignUpDto);
        }
        else if((provider.toUpperCase()).equals(Provider.KAKAO.getProvider())) {
            userDto = userSignUpDtoToUserDto(Provider.KAKAO, userSignUpDto);
        }
        else if((provider.toUpperCase()).equals(Provider.APPLE.getProvider())) {
            userDto = userSignUpDtoToUserDto(Provider.APPLE, userSignUpDto);
        }
        else if((provider.toUpperCase()).equals(Provider.GOOGLE.getProvider())) {
            userDto = userSignUpDtoToUserDto(Provider.GOOGLE, userSignUpDto);
        }

        User userEntity = User.builder(userDto.getProviderUserId())
                .userName(userDto.getUserName())
                .nickName(userDto.getNickName())
                .gender(userDto.getGender())
                .phoneNumber(userDto.getPhoneNumber())
                .role(userDto.getRole())
                .provider(userDto.getProvider())
                .essentialTerms(userDto.getEssentialTerms())
                .optionalTerm1(userDto.getOptionalTerm1())
                .build();
        userRepository.save(userEntity);
        TokenInfoDto tokenInfoDto = generateTokens(userDto, "Create user success");

        return tokenInfoDto;
    }

    // <-------------------- Sign-in part -------------------->
    public TokenInfoDto signInWithProviderUserId(String provider, UserRequestDto.UserSignInDto userSignInDto) {
        String userUniqueId = userSignInDto.getUserUniqueId();
        User userEntity = userRepository.findByProviderUserId(provider.toUpperCase() + "_" + userUniqueId).get();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        TokenInfoDto tokenInfoDto = generateTokens(userDto, "Token refreshed");

        return tokenInfoDto;
    }


    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private void checkIsSignedUp(String phoneNumber) {
        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if(userEntity.isPresent()) {
            throw new AlreadySignedUpException(userEntity.get().getProvider()); // "이미 가입된 유저입니다.({provider}로 가입)
        }
    }

    // <--- Methods for readability --->
    private UserDto userSignUpDtoToUserDto(Provider provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        UserDto userDto = new UserDto();
        userDto.setProviderUserId(provider.getProvider().toUpperCase() + "_" + userSignUpDto.getUserUniqueId());
        userDto.setUserName(userSignUpDto.getUserName());
        userDto.setNickName(userSignUpDto.getNickName());
        userDto.setGender(userSignUpDto.getGender());
        userDto.setPhoneNumber(userSignUpDto.getPhoneNumber());
        userDto.setRole(Role.ROLE_USER);
        userDto.setProvider(provider);
        userDto.setEssentialTerms(userSignUpDto.getEssentialTerms());
        userDto.setOptionalTerm1(userSignUpDto.getOptionalTerm1());

        return userDto;
    }

    private TokenInfoDto generateTokens(UserDto userDto, String message) {
        List<String> roles = Arrays.asList(userDto.getRole().getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(userDto.getProviderUserId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        redisService.setStringValue(refreshToken, userDto.getProviderUserId(), JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        TokenInfoDto tokenInfoDto = new TokenInfoDto("success", message, accessToken, refreshToken);

        return tokenInfoDto;
    }


    // <-------------------- Test part -------------------->
    public UserDto signInTestToken(Cookie[] cookies) {
        String accessToken = "";
        String refreshToken = "";
        for (Cookie cookie: cookies) {
            if(cookie.getName().equals(jwtTokenProvider.ACCESS_TOKEN_NAME)) {
                accessToken = cookie.getValue();
            }
            else if(cookie.getName().equals(jwtTokenProvider.REFRESH_TOKEN_NAME)) {
                refreshToken = cookie.getValue();
            }
        }

        List<String> findInfo = redisService.getListValue(refreshToken);
        String providerUserId = findInfo.get(0);
        User userEntity = userRepository.findByProviderUserId(providerUserId).get();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        return userDto;
    }

}
