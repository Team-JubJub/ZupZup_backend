package com.rest.api.auth.service;

import com.rest.api.auth.naver.NaverConstants;
import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.naver.vo.NaverProfileResponseVo;
import com.rest.api.auth.naver.vo.NaverProfileVo;
import domain.auth.Provider;
import domain.auth.Role;
import domain.auth.User;
import dto.auth.customer.UserDto;
import dto.auth.customer.request.UserRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import repository.UserRepository;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileOAuthService {  // For not a case of OAuth2

    @Autowired
    WebClient webClient;
    @Autowired
    NaverConstants naverConstants;
    private final UserRepository userRepository;

    // <-------------------- Sign-up part -------------------->
    public String signUp(String provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        checkIsSignedUp(userSignUpDto.getPhoneNumber());
        UserDto userDto = new UserDto();
        if(provider.equals(Provider.NAVER.getProvider().toLowerCase())) {
            userSignUpDtoToUserDto(Provider.NAVER, userSignUpDto);
        }
        else if(provider.equals(Provider.KAKAO.getProvider().toLowerCase())) {
            userSignUpDtoToUserDto(Provider.KAKAO, userSignUpDto);
        }
        else if(provider.equals(Provider.APPLE.getProvider().toLowerCase())) {
            userSignUpDtoToUserDto(Provider.APPLE, userSignUpDto);
        }

        User userEntity = User.builder(userDto.getProviderUserId())
                .nickName(userDto.getNickName())
                .gender(userDto.getGender())
                .phoneNumber(userDto.getPhoneNumber())
                .role(userDto.getRole())
                .provider(userDto.getProvider())
                .essentialTerms(userDto.getEssentialTerms())
                .optionalTerm1(userDto.getOptionalTerm1())
                .build();
        userRepository.save(userEntity);

        return "회원가입이 완료되었습니다";
    }
    // <-------------------- Sign-in part -------------------->


    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private void checkIsSignedUp(String phoneNumber) {
        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if(userEntity.isPresent()) {

        }
    }
    // <--- Methods for readability --->
    private UserDto userSignUpDtoToUserDto(Provider provider, UserRequestDto.UserSignUpDto userSignUpDto) {
        UserDto userDto = new UserDto();
        userDto.setProviderUserId(provider.getProvider().toUpperCase() + "_" + userSignUpDto.getUserUniqueId());
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
    public NaverLoginVo signInTest(Map<String, String> resValue) {  // 로그인 테스트 위해서 액세스토큰, 유저 ID 얻어오는 함수
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

}
