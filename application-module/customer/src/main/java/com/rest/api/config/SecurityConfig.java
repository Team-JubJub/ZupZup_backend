package com.rest.api.config;

import com.rest.api.auth.jwt.JwtAuthenticationFilter;
import com.rest.api.auth.jwt.JwtExceptionFilter;
import com.rest.api.auth.jwt.JwtTokenProvider;
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

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .requestMatchers( "/", "http://localhost:8082/**", "/swagger-ui/**", "/v3/api-docs/**") // H2, swagger permit all
                    .requestMatchers("/customer/**")
                    .requestMatchers(
                            "/mobile/sign-up/**",
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
                    .requestMatchers("/customer/**").permitAll() // 원래 있던 파트 로그인 없이 테스트할 수 있게 임시 처리)
                    .requestMatchers("/mobile/sign-up/**", "/mobile/sign-in/**", "/mobile/account-recovery").permitAll()    // 회원가입, 로그인, 계정 찾기 permit all
                    .anyRequest().authenticated()   // permitAll() 이외의 모든 request authenticated 처리
                .and()  // Filter로 JwtAuthenticationFilter 적용, 그 앞에 Exception handle 위해 JwtExceptionFilter 추가
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(new JwtExceptionFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

}
