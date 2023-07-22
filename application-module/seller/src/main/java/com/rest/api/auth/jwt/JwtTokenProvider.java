package com.rest.api.auth.jwt;

import com.rest.api.auth.redis.RedisService;
import com.rest.api.auth.service.CustomSellerDetailsService;
import com.rest.api.auth.dto.LoginInfoDto;
import dto.auth.token.customer.CustomerRefreshResultDto;
import io.jsonwebtoken.*;

import jakarta.annotation.PostConstruct;
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
    private final CustomSellerDetailsService customSellerDetailsService;
    @Value("${spring.security.jwt.secret}")
    private String secretKey;
    final static public long ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS = 1000L*60*30; // 30분
    final static public long REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 1000L*60*60*24*14;  // 2주
    final static public long APPLE_CLIENT_SECRET_VALIDITY_IN_MILLISECONDS = 1000L*60*60*24*30;  // 한 달(애플 기준은 6개월 미만)
    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";
    final static public String SUCCESS_STRING = "SUCCESS";
    final static public String FAIL_STRING = "FAILED";
    final static public String INVALID_ACCESS_TOKEN = "Invalid access token";
    final static public String EXPIRED_ACCESS_TOKEN = "Expired access token";

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public CustomerRefreshResultDto validateRefreshToken(String refreshToken)  // refresh token 유효성 검증, 새로운 access token 발급
    {
        List<String> findInfo = redisService.getListValue(refreshToken);    // 0 = loginId, 1 = refreshToken
        if (findInfo.get(0) == null) { // 유저 정보가 없으면 FAILED 반환
            return new CustomerRefreshResultDto(FAIL_STRING, "No user found", null, null);
        }
        if (validateToken(refreshToken))  // refresh Token 유효성 검증 완료 시
        {
            UserDetails findSeller = customSellerDetailsService.loadSellerByLoginId((String)findInfo.get(0));
            List<String> roles = findSeller.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());
            String newAccessToken = generateAccessToken((String)findInfo.get(0), roles);
            return new CustomerRefreshResultDto(SUCCESS_STRING, "Access token refreshed", findInfo.get(0), newAccessToken);
        }
        return new CustomerRefreshResultDto(FAIL_STRING, "Refresh token expired", null, null);  // refresh Token 만료 시
    }

    public boolean validateToken(String jwtToken) { // Jwt 토큰의 유효성 + 만료일자 확인
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
        return !claims.getBody().getExpiration().before(new Date());    // expire 된 게 아니라면 false + ! => true
    }

    public String generateAccessToken(String loginId, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(loginId);
        claims.put("roles", roles);
        Date now = new Date();
        String accessToken = Jwts.builder()
                .setClaims(claims) // 데이터
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() +  ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS)) // set Expire Time(30분)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();

        return accessToken;
    }

    public String generateRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        String refreshToken = Jwts.builder()   // Refresh token 생성
                .setIssuedAt(now)
                .setExpiration(validity)    // 2주
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return refreshToken;
    }

    public Authentication getAuthentication(String token) { // Jwt 토큰으로 인증 정보를 조회
        LoginInfoDto sellerDetails = ((LoginInfoDto) customSellerDetailsService.loadSellerByLoginId(this.getLoginId(token)));
        return new UsernamePasswordAuthenticationToken(sellerDetails, "", sellerDetails.getAuthorities());  // password(credentials)는 비우고 사용
    }

    public String getLoginId(String token) { // Jwt 토큰에서 회원 구별 정보(loginId) 추출
        try
        {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }
        catch (ExpiredJwtException e)
        {
            e.printStackTrace();
            return "expired";
        }
        catch (JwtException e)  // JWT 관련 모든 예외, 여기서 삭제할지 고민해볼 것
        {
            e.printStackTrace();
            return "invalid";
        }
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

    public Boolean isRedisBlackList(String accessToken)  // true -> 로그아웃 혹은 회원탈퇴한 상황
    {
        return redisService.getStringValue(accessToken) != null;    // redis에 accessToken이 저장돼있다면 로그아웃 혹은 회원탈퇴한 경우
    }

}
