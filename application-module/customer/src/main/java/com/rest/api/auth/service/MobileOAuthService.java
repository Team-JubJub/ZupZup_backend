package com.rest.api.auth.service;

import com.rest.api.auth.naver.NaverConstants;
import com.rest.api.auth.naver.vo.NaverProfileResponseVo;
import com.rest.api.auth.naver.vo.NaverProfileVo;
import domain.auth.User;
import dto.auth.customer.request.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import repository.UserRepository;

@Service
public class MobileOAuthService {  // For not a case of OAuth2

    @Autowired
    private WebClient webClient;
    private UserRepository userRepository;
    private NaverConstants naverConstants;

    // <-------------------- Sign-up part -------------------->

    // <-------------------- Sign-in part -------------------->
    public String naverOAuthLogin(String access_token, String refresh_token, UserRequestDto.UserOAuthSignInDto userOAuthSignInDto) {
        NaverProfileVo naverProfileVo = getNaverProfile(access_token);
        String userUniqueId = naverProfileVo.getId();
        User userEntity = userRepository.findByProviderUserId("NAVER_" + userUniqueId);

        return "temp";
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
                .header("Authorization", "Bearer" + access_token)
                .retrieve()
                .bodyToMono(NaverProfileResponseVo.class)
                .block()
                .getNaverProfileVo();   // NaverProfileResponseVo에서 naverProfileVo만 return

        return naverProfileVo;
    }
}
