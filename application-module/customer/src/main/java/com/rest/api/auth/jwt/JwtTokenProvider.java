package com.rest.api.auth.jwt;

import com.rest.api.auth.redis.RedisService;
import com.rest.api.auth.service.CustomUserDetailsService;
import com.rest.api.auth.dto.LoginInfoDto;
import dto.auth.token.RefreshResultDto;
import io.jsonwebtoken.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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
    final static public long ACCESS_TOKEN_VALIDITY_IN_MILLISECONDS = 1000L*60*30; // 30분
    final static public long REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS = 1000L*60*60*24*14;  // 2주
    final static public long APPLE_CLIENT_SECRET_VALIDITY_IN_MILLISECONDS = 1000L*60*60*24*30;  // 한 달(애플 기준은 6개월 미만)
    final static private String APPLE_KEY_ID = "CFGTY8R4TG";
    final static private String APPLE_TEAM_ID = "2S73QX9MMY";
    final static private String APPLE_BUNDLE_ID = "ZupZup.ZupZup";
    final static private String APPLE_P8_KEY_NAME = ""; // apple에서 다운받은 p8 인증서(resources에 위치)
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

    public RefreshResultDto validateRefreshToken(String refreshToken)  // refresh token 유효성 검증, 새로운 access token 발급
    {
        List<String> findInfo = redisService.getListValue(refreshToken);    // 0 = providerUserId, 1 = refreshToken
        if (findInfo.get(0).equals(null)) { // 유저 정보가 없으면 FAILED 반환
            return new RefreshResultDto(FAIL_STRING, "No user found", null, null);
        }
        if (validateToken(refreshToken))  // refresh Token 유효성 검증 완료 시
        {
            UserDetails findUser = customUserDetailsService.loadUserByProviderUserId((String)findInfo.get(0));
            List<String> roles = findUser.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());
            String newAccessToken = generateAccessToken((String)findInfo.get(0), roles);
            return new RefreshResultDto(SUCCESS_STRING, "Access token refreshed", findInfo.get(0), newAccessToken);
        }
        return new RefreshResultDto(FAIL_STRING, "Refresh token expired", null, null);  // refresh Token 만료 시
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

    public String generateAppleClientSecret() {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", APPLE_KEY_ID);
        jwtHeader.put("alg", "ES256");
        Date now = new Date();
        String appleClientSecret = null;
        Date validity = new Date(now.getTime() + APPLE_CLIENT_SECRET_VALIDITY_IN_MILLISECONDS);
        try {
            appleClientSecret = Jwts.builder()   // Refresh token 생성
                    .setHeaderParams(jwtHeader)
                    .setIssuer(APPLE_TEAM_ID)
                    .setIssuedAt(now) // 발행 시간 - UNIX 시간
                    .setExpiration(validity) // 만료 시간
                    .setAudience("https://appleid.apple.com")
                    .setSubject(APPLE_BUNDLE_ID)
                    .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                    .compact();
        } catch(IOException e) {
            return null;
        } catch(NoSuchAlgorithmException e) {
            return null;
        } catch(InvalidKeySpecException e) {
            return null;
        }

        return appleClientSecret;
    }

    private static PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        InputStream privateKey = new ClassPathResource(APPLE_P8_KEY_NAME).getInputStream();

        String result = new BufferedReader(new InputStreamReader(privateKey)).lines().collect(Collectors.joining("\n"));

        String key = result.replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);

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
