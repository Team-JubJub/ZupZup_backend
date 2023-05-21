package com.rest.api.auth.service;

import com.rest.api.auth.jwt.JwtTokenProvider;

import com.rest.api.auth.redis.RedisService;
import domain.auth.User.Provider;
import domain.auth.User.Role;
import domain.auth.User.User;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.UserRequestDto;
import dto.auth.customer.response.UserResponseDto;
import dto.auth.token.TokenInfoDto;
import exception.customer.AlreadySignUppedException;
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
public class MobileOAuthService {

    @Autowired
    ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    final static public String NO_USER_FOUND = "No user found";

    // <-------------------- Sign-up part -------------------->
    public TokenInfoDto signUp(String provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        checkIsSignUpped(userSignUpDto.getPhoneNumber());
        UserDto userDto = userSignUpDtoToUserDto(provider, userSignUpDto);

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

    public UserResponseDto.DeleteUserDto deleteUser(String provider, String accessToken, String refreshToken) {
        Long remainExpiration = jwtTokenProvider.remainExpiration(accessToken); // 남은 expiration을 계산함.
        UserResponseDto.DeleteUserDto deleteUserDto = new UserResponseDto.DeleteUserDto(null, null);
        if (remainExpiration >= 1) {   // 만료 직전 혹은 만료된 토큰이 아니라면
            deleteUserDto.setMessage(jwtTokenProvider.SUCCESS_STRING);
            if (provider.equals(Provider.APPLE.getProvider())) {
                deleteUserDto.setClientSecret(jwtTokenProvider.generateAppleClientSecret());
            }
            String providerUserId = jwtTokenProvider.getProviderUserId(accessToken);
            User userEntity = userRepository.findByProviderUserId(providerUserId).get();    // delete()와 deleteById() 모두 findBy로 유저 엔티티 찾는 과정은 거침. 예외 처리를 직접 하는 것이냐 아니냐의 차이인데, 일단 이렇게 적용하고 delete()가 더 나을지 고민해볼 것.
            userRepository.deleteById(userEntity.getUserId());  // RDB에서 유저 삭제
            redisService.deleteKey(refreshToken); // refreshToken을 key로 하는 데이터 redis에서 삭제
            redisService.setStringValue(accessToken, "deleted-user", remainExpiration); // access token 저장(key: acc_token, value: "deleted-user")

            return deleteUserDto;
        }
        deleteUserDto.setMessage(jwtTokenProvider.EXPIRED_ACCESS_TOKEN);

        return deleteUserDto;   // 만료된 access token인 경우
    }

    public Boolean nickNameCheck(String nickName) {
        Optional<User> userEntity = userRepository.findByNickName(nickName);
        if(userEntity.isPresent()) {
            return true;
        }

        return false;
    }

    // <-------------------- Sign-in part -------------------->
    public TokenInfoDto signInWithProviderUserId(String provider, UserRequestDto.UserSignInDto userSignInDto) {
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
    // <--- Methods for error handling --->
    private void checkIsSignUpped(String phoneNumber) {
        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if(userEntity.isPresent()) {
            throw new AlreadySignUppedException(userEntity.get().getProvider()); // "User already sign-upped.(Platform with: {provider})
        }
    }

    // <--- Methods for readability --->
    private UserDto userSignUpDtoToUserDto(String provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        UserDto userDto = new UserDto();
        if((provider.toUpperCase()).equals(Provider.NAVER.getProvider())) {
            userDto.setProvider(Provider.NAVER);
        }
        else if((provider.toUpperCase()).equals(Provider.KAKAO.getProvider())) {
            userDto.setProvider(Provider.KAKAO);
        }
        else if((provider.toUpperCase()).equals(Provider.APPLE.getProvider())) {
            userDto.setProvider(Provider.APPLE);
        }
        else if((provider.toUpperCase()).equals(Provider.GOOGLE.getProvider())) {
            userDto.setProvider(Provider.GOOGLE);
        }
        userDto.setProviderUserId(provider.toUpperCase() + "_" + userSignUpDto.getUserUniqueId());
        userDto.setUserName(userSignUpDto.getUserName());
        userDto.setNickName(userSignUpDto.getNickName());
        userDto.setGender(userSignUpDto.getGender());
        userDto.setPhoneNumber(userSignUpDto.getPhoneNumber());
        userDto.setRole(Role.ROLE_USER);
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
    public UserDto signInTestToken(String accessToken, String refreshToken) {
        List<String> findInfo = redisService.getListValue(refreshToken);
        String providerUserIdRefresh = findInfo.get(0);
        System.out.println(providerUserIdRefresh);

        String providerUserIdAccess = jwtTokenProvider.getProviderUserId(accessToken);
        User userEntity = userRepository.findByProviderUserId(providerUserIdAccess).get();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        return userDto;
    }

}
