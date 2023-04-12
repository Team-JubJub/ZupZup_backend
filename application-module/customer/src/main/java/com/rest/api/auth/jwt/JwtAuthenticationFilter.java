package com.rest.api.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Null;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // JwtAuthenticationFilter를 filterChain에 등록
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = null;
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        try {
            if (cookies != null) {   // 쿠키가 있다면
                accessToken = jwtTokenProvider.getCookie((HttpServletRequest) request, JwtTokenProvider.ACCESS_TOKEN_NAME).getValue();
                if (!jwtTokenProvider.isLoggedOut(accessToken)) {   // 로그아웃 된 상황이 아니라면(redis refreshToken 테이블에 accessToken이 저장된 게 아니라면)
                    try {
                        if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {
                            Authentication auth = jwtTokenProvider.getAuthentication(accessToken);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    } catch (ExpiredJwtException e) {   // validateToken의 claims.getBody().getExpiration()에서 발생
                        System.out.println("Validation failed");
                        // 재발급 로직 구현하기
                    }
                }
            }
        } catch (NullPointerException e) {
            // 쿠키가 필요한 요청에 대해 쿠키가 없는 경우 예외처리 로직 구현하기
        }
        filterChain.doFilter(request, response);
    }

}
