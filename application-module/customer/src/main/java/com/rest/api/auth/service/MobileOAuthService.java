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
public class MobileOAuthService {  // For not a case of OAuth2

    @Autowired
    ModelMapper modelMapper;
//    @Autowired
//    WebClient webClient;
//    @Autowired
//    NaverConstants naverConstants;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    // <-------------------- Sign-up part -------------------->
    public TokenInfoDto signUp(String provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        checkIsSignedUp(userSignUpDto.getPhoneNumber());
        UserDto userDto = new UserDto();
        if((provider.toUpperCase()).equals(Provider.NAVER.getProvider())) {
            System.out.println("naver sign up");
            userDto = userSignUpDtoToUserDto(Provider.NAVER, userSignUpDto);
        }
        else if((provider.toUpperCase()).equals(Provider.KAKAO.getProvider())) {
            userDto = userSignUpDtoToUserDto(Provider.KAKAO, userSignUpDto);
        }
        else if((provider.toUpperCase()).equals(Provider.APPLE.getProvider())) {
            userDto = userSignUpDtoToUserDto(Provider.APPLE, userSignUpDto);
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
//        String providerAccessToken = userSignInDto.getProviderAccessToken();

//        if((provider.toUpperCase()).equals(Provider.NAVER.getProvider())) {
//            System.out.println("naver sign in");
//            if(!isUserOfNaver(providerAccessToken, userUniqueId)) {
//                throw new UserInfoNotMatchException();
//            }
//        }
//        else if((provider.toUpperCase()).equals(Provider.KAKAO.getProvider())) {
//            // 카카오에 정보 요청 로직
//        }
//        else if((provider.toUpperCase()).equals(Provider.APPLE.getProvider())) {
//            // 애플에 정보 요청 로직
//        }
//        else if((provider.toUpperCase()).equals(Provider.GOOGLE.getProvider())) {
//            // 구글에 정보 요청 로직
//        }
        User userEntity = userRepository.findByProviderUserId(provider.toUpperCase() + "_" + userUniqueId).get();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        TokenInfoDto tokenInfoDto = generateTokens(userDto, "Token refreshed");

        return tokenInfoDto;
    }
//    // <--- Sign-in Naver part --->
//    private boolean isUserOfNaver(String providerAccessToken, String userUniqueId) {
//        String fromNaver = getNaverProfile(providerAccessToken).getId();
//        if (!fromNaver.equals(userUniqueId)) { // 네이버에 요청해 얻은 유저의 id와 다르면
//            return false;
//        }
//        return true;
//    }
//    private NaverProfileVo getNaverProfile(String accessToken) {   // 여기서 한 번 더 인증거치는 걸로. (NaverProfileResponseVo에서 상태코드, 메세지 확인하는 방법 알아보기)
//        final String profileUri = UriComponentsBuilder
//                .fromUriString(naverConstants.getUser_info_uri())
//                .build()
//                .encode()
//                .toUriString();
//
//        NaverProfileVo naverProfileVo = webClient
//                .get()
//                .uri(profileUri)
//                .header("Authorization", "Bearer " + accessToken)
//                .retrieve()
//                .bodyToMono(NaverProfileResponseVo.class)
//                .block()
//                .getResponse();   // NaverProfileResponseVo에서 naverProfileVo만 return
//
//        return naverProfileVo;
//    }
//    // <--- Sign-in Kakao part --->
//    // <--- Sign-in Apple part --->
//    // <--- Sign-in Google part --->


    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private void checkIsSignedUp(String phoneNumber) {
        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if(userEntity.isPresent()) {
            throw new AlreadySignedUpException(userEntity.get().getProvider());
        }
    }

    // <--- Methods for readability --->
    private UserDto userSignUpDtoToUserDto(Provider provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        UserDto userDto = new UserDto();
        userDto.setProviderUserId(provider.getProvider().toUpperCase() + "_" + userSignUpDto.getUserUniqueId());
        System.out.println(userDto.getProviderUserId());
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

//    public NaverLoginVo signInTestNaver(Map<String, String> resValue) {  // 로그인 테스트 위해서 액세스토큰, 유저 ID 얻어오는 함수
//        final String uri = UriComponentsBuilder
//                .fromUriString(naverConstants.getToken_uri())
//                .queryParam("grant_type", "authorization_code")
//                .queryParam("client_id", naverConstants.getClient_id())
//                .queryParam("client_secret", naverConstants.getClient_secret())
//                .queryParam("code", resValue.get("code"))
//                .queryParam("state", resValue.get("state"))
//                .queryParam("refresh_token", resValue.get("refresh_token"))
//                .build()
//                .encode()
//                .toUriString();
//
//        NaverLoginVo naverLoginVo =  webClient
//                .get()
//                .uri(uri)
//                .retrieve()
//                .bodyToMono(NaverLoginVo.class)
//                .block();
//        NaverProfileVo naverProfileVo = getNaverProfile(naverLoginVo.getAccess_token());
//        System.out.println("User Id: " + naverProfileVo.getId()); // zupzup에 로그인 요청 시 body로 실을 유저 ID
//
//        return naverLoginVo;
//    }

}
