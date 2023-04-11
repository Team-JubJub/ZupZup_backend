package com.rest.api.auth.service;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.naver.NaverConstants;
import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.naver.vo.NaverProfileResponseVo;
import com.rest.api.auth.naver.vo.NaverProfileVo;


import com.rest.api.auth.redis.RedisService;
import domain.auth.Token.RefreshToken;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileOAuthService {  // For not a case of OAuth2

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    WebClient webClient;
    @Autowired
    NaverConstants naverConstants;
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;

    // <-------------------- Sign-up part -------------------->
    public TokenInfoDto signUp(String provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        checkIsSignedUp(userSignUpDto.getPhoneNumber());
        UserDto userDto = new UserDto();
        if(provider.equals(Provider.NAVER.getProvider().toLowerCase())) {
            System.out.println("naver sign up");
            userDto = userSignUpDtoToUserDto(Provider.NAVER, userSignUpDto);
        }
        else if(provider.equals(Provider.KAKAO.getProvider().toLowerCase())) {
            userDto = userSignUpDtoToUserDto(Provider.KAKAO, userSignUpDto);
        }
        else if(provider.equals(Provider.APPLE.getProvider().toLowerCase())) {
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

        List<String> roles = Arrays.asList(userDto.getRole().getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(userDto.getProviderUserId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        redisService.setStringValue(refreshToken, userDto.getProviderUserId(), JwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);

        TokenInfoDto tokenInfoDto = new TokenInfoDto("success", "Create user success", accessToken, refreshToken);

        return tokenInfoDto;
    }
    // <-------------------- Sign-in part -------------------->
    public void signIn() {

    }

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

    // <--- Methods for test --->
    public UserDto signInTestToken(Cookie[] cookies) {
        String accessToken = "";
        String refreshToken = "";
        for (Cookie cookie: cookies) {
            if (cookie.getValue() == null) {    // 쿠키 값이 null 인 것에 대해 패스
                String cookieName = cookie.getName();
                System.out.println("Null cookie name: " + cookieName);
                System.out.println("Null cookie attribute: " + cookie.getAttribute(cookieName));
                continue;
            }
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


    public NaverLoginVo signInTestNaver(Map<String, String> resValue) {  // 로그인 테스트 위해서 액세스토큰, 유저 ID 얻어오는 함수
        final String uri = UriComponentsBuilder
                .fromUriString(naverConstants.getToken_uri())
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", naverConstants.getClient_id())
                .queryParam("client_secret", naverConstants.getClient_secret())
                .queryParam("code", resValue.get("code"))
                .queryParam("state", resValue.get("state"))
                .queryParam("refresh_token", resValue.get("refresh_token"))
                .build()
                .encode()
                .toUriString();

        NaverLoginVo naverLoginVo =  webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(NaverLoginVo.class)
                .block();
        NaverProfileVo naverProfileVo = getNaverProfile(naverLoginVo.getAccess_token());
        System.out.println(naverProfileVo.getId()); // zupzup에 로그인 요청 시 body로 실을 유저 ID

        return naverLoginVo;
    }

    private NaverProfileVo getNaverProfile(String access_token) {   // 여기서 한 번 더 인증거치는 걸로. (NaverProfileResponseVo에서 상태코드, 메세지 확인하는 방법 알아보기)
        final String profileUri = UriComponentsBuilder
                .fromUriString(naverConstants.getUser_info_uri())
                .build()
                .encode()
                .toUriString();

        NaverProfileVo naverProfileVo = webClient
                .get()
                .uri(profileUri)
                .header("Authorization", "Bearer " + access_token)
                .retrieve()
                .bodyToMono(NaverProfileResponseVo.class)
                .block()
                .getResponse();   // NaverProfileResponseVo에서 naverProfileVo만 return

        return naverProfileVo;
    }

}
