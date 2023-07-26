package com.rest.api.auth.jwt;

import exception.auth.RefreshRequiredException;
import exception.auth.RequiredHeaderNotExistException;
import exception.auth.BlackListTokenException;
import exception.auth.SignFailedException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);    // SecurityConfig에서 JwtAuthenticationFilter 이전에 이 필터를 등록, JwtAuthenticationFilter에서 발생하는 예외를 여기서 핸들링.
        } catch (RequiredHeaderNotExistException e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write(e.getMessage());
        } catch (RefreshRequiredException e) {  // 액세스 토큰 만료 시
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
        } catch (BlackListTokenException e) {   // 로그아웃, 회원탈퇴 된 회원의 액세스토큰으로 요청한 경우
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
        } catch (SignFailedException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
        } catch (MalformedJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(e.getMessage());
        }
    }

}
