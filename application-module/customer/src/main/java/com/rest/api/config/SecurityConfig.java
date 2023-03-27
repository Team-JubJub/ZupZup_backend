package com.rest.api.config;

import com.rest.api.auth.handler.OAuth2AuthenticationFailureHandler;
import com.rest.api.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.rest.api.auth.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {    // Http Security part
        http
                    .csrf().disable()
                    .headers().frameOptions().disable()
                .and()
                    .authorizeHttpRequests()    // authorizeRequests() -> authorizeHttpRequests()
                    .requestMatchers("/login/**").authenticated()       // antMatchers() -> requestMatchers()
                    .requestMatchers("/customer/**", "/h2-console/**").permitAll()  // 원래 있던 파트 로그인 없이 테스트할 수 있게 임시 처리
//                .and()
//                .logout()
//                .logoutSuccessUrl("/")
                .and()
                    .oauth2Login().loginPage("/login/oauth2")    // 인증 필요한 URL 접근 시 이동할 login page
                    .defaultSuccessUrl("/") // 로그인 성공 시 이동할 곳
                    .failureUrl("/login")   // 로그인 실패 시 이동할 곳
                    .userInfoEndpoint().userService(customOAuth2UserService)   // // 로그인 성공 후 사용자 정보를 가져옴, 사용자 정보 처리 시 사용 될 service
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler);

        return http.build();
    }


}
