package com.rest.api.config;

import com.rest.api.auth.handler.OAuth2AuthenticationFailureHandler;
import com.rest.api.auth.handler.OAuth2AuthenticationSuccessHandler;
import com.rest.api.auth.jwt.JwtAuthenticationFilter;
import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.rest.api.auth.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomOAuth2UserService customOAuth2UserService, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieOAuth2AuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
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
                    .requestMatchers( "http://localhost:8082/**", "/customer/**", "/h2-console/**").permitAll()  // 원래 있던 파트 로그인 없이 테스트할 수 있게 임시 처리
//                .and()
//                .logout()
//                .logoutSuccessUrl("/")
                .and()
                    .oauth2Login()
                    .authorizationEndpoint().baseUri("/login/oauth2")
                    .authorizationRequestRepository(cookieOAuth2AuthorizationRequestRepository())
                .and()
                    .redirectionEndpoint()
                    .baseUri("/login/oauth2/code/**")   // 일단 이렇게 두고 나중에 수정
//                    .loginPage("/login/oauth2")    // 인증 필요한 URL 접근 시 이동할 login page
                .and()
                    .userInfoEndpoint().userService(customOAuth2UserService)   // // 로그인 성공 후 사용자 정보를 가져옴, 사용자 정보 처리 시 사용 될 service
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
                .and()
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
