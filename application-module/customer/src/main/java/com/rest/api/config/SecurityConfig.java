package com.rest.api.config;

import com.rest.api.auth.jwt.JwtAuthenticationFilter;
import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.rest.api.auth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomOAuth2UserService customOAuth2UserService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customOAuth2UserService = customOAuth2UserService;
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
                    .cors(c -> {
                        CorsConfigurationSource source = request -> {
                            // Cors 허용 패턴
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(
                                    List.of("*")
                            );
                            config.setAllowedMethods(
                                    List.of("*")
                            );
                            return config;
                        };
                        c.configurationSource(source);
                    })
                    .httpBasic().disable()
                    .csrf().disable()
                    .headers().frameOptions().disable()
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests()    // authorizeRequests() -> authorizeHttpRequests()
                    .requestMatchers("/mobile/test/sign-in").authenticated()
                    .requestMatchers( "/", "http://localhost:8082/**", "/mobile/sign-up/**", "/mobile/sign-in/**", "/customer/**", "/h2-console/**", "/login/oauth2/callback/**").permitAll()  // 원래 있던 파트 로그인 없이 테스트할 수 있게 임시 처리
                    .anyRequest().permitAll()
                .and()  // Filter로 JwtAuthenticationFilter 적용
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
