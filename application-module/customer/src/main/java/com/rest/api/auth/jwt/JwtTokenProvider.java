package com.rest.api.auth.jwt;

import com.rest.api.auth.service.RedisService;
import domain.auth.Token.response.ValidRefreshTokenResponse;
import io.jsonwebtoken.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisService redisService;
    @Value("${spring.security.jwt.secret}")
    private String secretKey;
    public static final long ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS = 100*60*30; // 30분
    public static final long REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 100*60*60*24*14;  // 2주
    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public ValidRefreshTokenResponse validateRefreshToken(String accessToken, String refreshToken)
    {
        List<String> findInfo = redisService.getListValue(refreshToken);
        String providerUserId = getProviderUserId(accessToken);
        if (findInfo.get(0).equals(null)) { // 유저 정보가 없으면 401 반환
            return new ValidRefreshTokenResponse(null, 401, null);
        }
        if (providerUserId.equals(findInfo.get(0)) && validateToken(refreshToken))
        {
            UserDetails findMember = userDetailsService.loadUserByUsername((String)findInfo.get(0));
            List<String> roles = findMember.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());
            String newAccessToken = generateAccessToken((String)findInfo.get(0), roles);
            return new ValidRefreshTokenResponse((String)findInfo.get(0), 200, newAccessToken);
        }
        return new ValidRefreshTokenResponse(null, 403, null);
    }

    public String generateAccessToken(String payload) {
        Claims claims = Jwts.claims().setSubject(payload);
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

        return Jwts.builder()   // Access token 생성
                .setIssuedAt(now)
                .setExpiration(validity)    // 30초
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);   // 토큰 복호화

        if(claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // Jwt 토큰으로 인증 정보를 조회
    public Authentication getAuthentication(String token) {
        LoginInfo userDetails = ((LoginInfo)userDetailsService.loadUserByUsername(this.getProviderUserId(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // Jwt 토큰에서 회원 구별 정보 추출
    public String getProviderUserId(String token) {
        try
        {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }
        catch (ExpiredJwtException e)
        {
            //e.printStackTrace();
            return "Expired";
        }
        catch (JwtException e)
        {
            //e.printStackTrace();
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

    // Request의 Header에서 token 파싱
    public String resolveToken(HttpServletRequest req, String headerName) {
        return req.getHeader(headerName);
    }

    // Jwt 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        return !claims.getBody().getExpiration().before(new Date());
    }

    public Long remainExpiration(String token)
    {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getExpiration().getTime() - new Date().getTime();
        }
        catch (ExpiredJwtException e) {
            return -1L;
        }
    }

    public Boolean isLoggedOut(String accessToken)
    {
        if (accessToken == null)
            return false;
        return redisService.getStringValue(accessToken) != null;
    }

}
