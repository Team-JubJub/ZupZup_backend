package com.rest.api.auth.jwt;

import exception.customer.RefreshRequiredException;
import exception.customer.SignOutedUserException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // JwtAuthenticationFilter를 filterChain에 등록
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String accessToken = jwtTokenProvider.resolveToken(request, jwtTokenProvider.ACCESS_TOKEN_NAME);
        if (accessToken != null) {  // 헤더에 access token이 존재한다면
            if (!jwtTokenProvider.isRedisBlackList(accessToken)) {   // 로그아웃 된 상황이 아니라면(redis refreshToken 테이블에 accessToken이 저장된 게 아니라면)
                try {
                    if (accessToken != null && jwtTokenProvider.validateToken(accessToken)) {   // access token이 만료되지 않았을 경우
                        Authentication auth = jwtTokenProvider.getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (ExpiredJwtException e) {   // validateToken의 claims.getBody().getExpiration()에서 발생
                    System.out.println("Validation failed");
                    throw new RefreshRequiredException();
                }
            }
            else {  // log out된 유저
                System.out.println("Sign-outed user");
                throw new SignOutedUserException();
            }
        }

        filterChain.doFilter(request, response);
    }

}
