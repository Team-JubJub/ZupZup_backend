package com.rest.api.auth.naver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class NaverConstants {

    private String client_id;
    private String client_secret;
    private String authorization_uri;
    private String token_uri;
    private String user_info_uri;

    @Value("${spring.security.oauth2.client.registration.naver.client_id}")
    private void setClient_id(String client_id) {
        this.client_id = client_id;
    }
    @Value("${spring.security.oauth2.client.registration.naver.client_secret}")
    private void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }
    @Value("${spring.security.oauth2.client.provider.naver.authorization_uri}")
    private void setAuthorization_uri(String authorization_uri) {
        this.authorization_uri = authorization_uri;
    }
    @Value("${spring.security.oauth2.client.provider.naver.token_uri}")
    private void setToken_uri(String token_uri) {
        this.token_uri = token_uri;
    }
    @Value("${spring.security.oauth2.client.provider.naver.user_info_uri}")
    private void setUser_info_uri(String user_info_uri) {
        this.user_info_uri = user_info_uri;
    }

}
