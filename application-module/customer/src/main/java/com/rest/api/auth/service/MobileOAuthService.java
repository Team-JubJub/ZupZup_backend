package com.rest.api.auth.service;

import com.rest.api.auth.naver.NaverConstants;
import com.rest.api.auth.naver.vo.NaverLoginVo;
import com.rest.api.auth.naver.vo.NaverProfileResponseVo;
import com.rest.api.auth.naver.vo.NaverProfileVo;
import domain.auth.User;
import dto.auth.customer.request.UserRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // <-------------------- Sign-in part -------------------->
    public String naverOAuthSignIn(String access_token, String refresh_token, UserRequestDto.UserOAuthSignInDto userOAuthSignInDto) {
        String result = "";
        NaverProfileVo naverProfileVo = getNaverProfile(access_token);   // 클라이언트에서 제공한 access token을 이용, NAVER에 유저 ID요청
        String userUniqueId = naverProfileVo.getId();
        if(userUniqueId.equals(userOAuthSignInDto.getUserUniqueId())) { // 요청한 유저 ID와 클라이언트에서 body에 실어 보낸 유저 ID가 같으면 1차 인증 성공
            System.out.println("Authentication success");
            Optional<User> userEntity = userRepository.findByProviderUserId("NAVER_" + userUniqueId);
            if(userEntity.isPresent()) {    // 줍줍에 가입이 된 회원의 경우 -> 로그인 처리
                result = "SingIn";
            }
            else {  // 최초 로그인인 경우 -> 회원가입으로
                result = "SignUp";
            }
        }
        else {  // 다르다면 1차 인증 실패 -> 회원 정보가 다릅니다(?) 리턴
            System.out.println("Authentication failed");
        }

        return result;
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    // <--- Methods for readability --->
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

    // <--- Methods for test --->
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
