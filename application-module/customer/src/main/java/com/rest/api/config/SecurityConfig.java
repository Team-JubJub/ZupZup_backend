package com.rest.api.config;

import com.rest.api.auth.jwt.JwtAuthenticationFilter;
import com.rest.api.auth.jwt.JwtTokenProvider;
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

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
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
                    .requestMatchers( "/", "http://localhost:8082/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // H2, swagger permit all
                    .requestMatchers("/customer/**").permitAll() // 원래 있던 파트 로그인 없이 테스트할 수 있게 임시 처리)
                    .requestMatchers("/mobile/sign-up/**", "/mobile/sign-in/**").permitAll()    // 회원가입, 로그인 permit all
//                    .requestMatchers("/login", "/mobile/sign-in/oauth2/callback/**").permitAll() // For login test
//                    .requestMatchers("/mobile/test/sign-in").authenticated()  // For Login test
//                .and()
//                    .oauth2Login()  // For login test
                .and()  // Filter로 JwtAuthenticationFilter 적용
                    .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
