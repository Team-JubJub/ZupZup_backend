package com.rest.api.auth.service;

import domain.auth.Provider;
import domain.auth.Role;
import domain.auth.User;
import dto.auth.customer.UserDto;
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
        OAuth2UserService delegate = new DefaultOAuth2UserService();    // 기본 처리 Service를 내가 정의한 Service에 위임함.
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//        String email;
        Role role = Role.ROLE_USER; // 사용자 앱에서의 로그인이므로, ROLE_USER 부여
        Provider provider;  // 각 플랫폼에 따라 추후에 설정
        String providedId;  // 각 플랫폼에서 제공하는 유니크 ID, DB에서 조회 및 최초 로그인 판단에 사용
        User userEntity;
        UserDto userDto = new UserDto();

        // 서비스 구분을 위한 작업(구글 : google, 네이버 : naver, ...)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> response = oAuth2User.getAttributes();

//        if(registrationId.equals(Provider.GOOGLE)) {   // In case of Google Login
//            email = (String)response.get("email");
//            userDto.setProvider(Provider.GOOGLE);
//            userDto.setEmail(email);
//        }
        if(registrationId.equals(Provider.NAVER)) {   // In case of Naver Login // temp
            provider = Provider.NAVER;
            Map<String, Object> hash = (Map<String, Object>)response.get("response");
            providedId = provider.getProvider() + "_" + "temp";   // ex) NAVER_uniqueID
            userDto.setProvidedId(providedId);
            userDto.setProvider(provider);
            userDto.setRole(role);
        }
        else {
            throw new OAuth2AuthenticationException("구현되지 않은 인증입니다.");
        }

        Optional<User> optionalUserEntity = userRepository.findByProvidedId(providedId);  // User가 DB에 있는지 없는지 여부 확인 용 -> 각 플랫폼에서 제공하는 user의 unique ID 이용
        if(optionalUserEntity.isPresent()) {  // 이미 가입한(ZupZup에) user에 대한 로직
            userEntity = optionalUserEntity.get();
        }
        else {  // 가입하지 않은 user -> DB에 저장   // temp
            userEntity = User.builder(userDto.getProvidedId())
                    .role(Role.ROLE_USER)
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
