package com.rest.api.auth.naver;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class NaverConstants {

    @Value("${spring.security.oauth2.client.registration.naver.client_id}")
    private String client_id;
    @Value("${spring.security.oauth2.client.registration.naver.client_secret}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.provider.naver.authorization_uri}")
    private String authorization_uri;
    @Value("${spring.security.oauth2.client.provider.naver.token_uri}")
    private String token_uri;
    @Value("${spring.security.oauth2.client.provider.naver.user_info_uri}")
    private String user_info_uri;

}
