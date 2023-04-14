package com.rest.api.auth.jwt;

import com.rest.api.auth.redis.RedisService;
import com.rest.api.auth.service.CustomUserDetailsService;
import com.rest.api.auth.dto.LoginInfoDto;
import dto.auth.token.RefreshResultDto;
import io.jsonwebtoken.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisService redisService;
    private final CustomUserDetailsService customUserDetailsService;
    @Value("${spring.security.jwt.secret}")
    private String secretKey;
    public static final long ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS = 1000L*60*30; // 30분
    public static final long REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 1000L*60*60*24*14;  // 2주
    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public RefreshResultDto validateRefreshToken(String refreshToken)  // refresh token 유효성 검증, 새로운 access token 발급
    {
        List<String> findInfo = redisService.getListValue(refreshToken);    // 0 = providerUserId, 1 = refreshToken
        if (findInfo.get(0).equals(null)) { // 유저 정보가 없으면 401 반환
            return new RefreshResultDto("failed", "No user found", null, null);
        }
        if (validateToken(refreshToken))  // refresh Token 유효성 검증 완료 시
        {
            UserDetails findUser = customUserDetailsService.loadUserByProviderUserId((String)findInfo.get(0));
            List<String> roles = findUser.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());
            String newAccessToken = generateAccessToken((String)findInfo.get(0), roles);
            return new RefreshResultDto("success", "Access token refreshed", findInfo.get(0), newAccessToken);
        }
        return new RefreshResultDto("failed", "Refresh token expired", null, null);  // refresh Token 만료 시
    }

    public boolean validateToken(String jwtToken) { // Jwt 토큰의 유효성 + 만료일자 확인
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        return !claims.getBody().getExpiration().before(new Date());    // expire 된 게 아니라면 false + ! => true
    }

    public String generateAccessToken(String providerUserId, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(providerUserId);
        claims.put("roles", roles);
        Date now = new Date();
        String accessToken = Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() +  ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();

        return accessToken;
    }

    public String generateRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        String refreshToken = Jwts.builder()   // Access token 생성
                .setIssuedAt(now)
                .setExpiration(validity)    // 30초
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return refreshToken;
    }

    public Authentication getAuthentication(String token) { // Jwt 토큰으로 인증 정보를 조회
        LoginInfoDto userDetails = ((LoginInfoDto) customUserDetailsService.loadUserByProviderUserId(this.getProviderUserId(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());  // password(credentials)는 비우고 사용
    }

    public String getProviderUserId(String token) { // Jwt 토큰에서 회원 구별 정보(providerUserId) 추출
        try
        {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }
        catch (ExpiredJwtException e)
        {
            e.printStackTrace();
            return "Expired";
        }
        catch (JwtException e)
        {
            e.printStackTrace();
            return "Invalid";
        }
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName)
    {
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

    public String resolveToken(HttpServletRequest request, String headerName) { // Request의 Header에서 token 파싱
        return request.getHeader(headerName);
    }

    public Long remainExpiration(String token)  // 토큰의 남은 유효기간
    {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().getTime() - new Date().getTime();
        }
        catch (ExpiredJwtException e) {
            return -1L;
        }
    }

    public Boolean isLoggedOut(String accessToken)  // true -> 로그아웃된 상황
    {
        if (accessToken == null)    // cookie의 access token 값이 null인 경우(만료)
            return false;
        return redisService.getAccessTokenValue(accessToken) != null;    // redis에 accesstoken이 저장돼있다면 로그아웃된 경우
    }

}
