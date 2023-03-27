package com.rest.api.auth.service;

import domain.auth.Provider;
import domain.auth.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Autowired
    UserRepository userRepository;
    @Autowired
    HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 서비스 구분을 위한 작업(구글 : google, 네이버 : naver, ...)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        String email;
        Map<String, Object> response = oAuth2User.getAttributes();

        if(registrationId.equals(Provider.GOOGLE)) {   // In case of Google Login
            email = (String)response.get("email");
        }
//        else if(registrationId.equals("naver")) {   // In case of Naver Login
//            Map<String, Object> hash = (Map<String, Object>)response.get("response");
//            email = (String)hash.get("email");
//        }
        else {
            throw new OAuth2AuthenticationException("아직 구현되지 않은 인증입니다.");
        }

        User userEntity;
        Optional<User> optionalUserEntity = userRepository.findByEmail(email);

        if(optionalUserEntity.isPresent()) {  // 이미 가입한(ZupZup에) 사람에 대한 로직
            userEntity = optionalUserEntity.get();
        }
        else {  // 가입하지 않은 유저 -> DB에 저장
            userEntity = User.builder(email)
                    .build();
            userRepository.save(userEntity);
        }
        httpSession.setAttribute("user", userEntity);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(userEntity.getRole().toString()))
                , oAuth2User.getAttributes()
                , userNameAttributeName);
    }

}
