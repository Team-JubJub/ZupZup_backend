package com.rest.api.config;

import com.zupzup.untact.social.jwt.SocialJwtAuthenticationFilter;
import com.zupzup.untact.social.jwt.SocialJwtExceptionFilter;
import com.zupzup.untact.social.jwt.SocialJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final SocialJwtTokenProvider jwtTokenProvider;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .requestMatchers( "", "/", "/error/**", "http://localhost:8082/**", "/swagger-ui/**", "/v3/api-docs/**") // H2, swagger permit all
                    .requestMatchers(
                            "/mobile/account", "/mobile/account/", "/mobile/account/{provider}", "/mobile/account/apple/withdraw", "/mobile/account/apple/refresh-token",
                            "/mobile/account/nickname-check",
                            "/mobile/sign-in/**",
                            "/mobile/account-recovery"
                    );
        };
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
                    .csrf().disable()   // 쿠키를 통한 보안 인증을 하지 않으면 disable 처리해도 좋다고 함. 토큰 전달 방식 쿠키 -> 헤더로 변경할 것.
                    .httpBasic().disable()  // http basic Auth 기반 인증 창(httpBasic) 안뜨게(disable)
                    .headers().frameOptions().disable() // iFrame과 관련한 보안 설정을 disable(현재는 H2 콘솔의 사용을 위해 disable처리)
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeHttpRequests()    // authorizeRequests() -> authorizeHttpRequests()
                    .requestMatchers( "/", "http://localhost:8082/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // H2, swagger permit all
                    .requestMatchers("/mobile/sign-up/**", "/mobile/sign-in/**", "/mobile/account-recovery").permitAll()    // 회원가입, 로그인, 계정 찾기 permit all
                    .anyRequest().authenticated()   // permitAll() 이외의 모든 request authenticated 처리
                .and()  // Filter로 CustomJwtAuthenticationFilter 적용, 그 앞에 Exception handle 위해 SocialJwtExceptionFilter 추가
                    .addFilterBefore(new SocialJwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new SocialJwtExceptionFilter(), SocialJwtAuthenticationFilter.class);

        return http.build();
    }

}
