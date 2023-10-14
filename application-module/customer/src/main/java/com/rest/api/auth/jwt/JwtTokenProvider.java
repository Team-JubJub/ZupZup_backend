package com.rest.api.auth.jwt;

import com.rest.api.auth.redis.RedisService;
import com.rest.api.auth.service.CustomUserDetailsService;
import com.rest.api.auth.dto.LoginInfoDto;
import dto.auth.token.customer.CustomerRefreshResultDto;
import exception.auth.customer.AppleWithdrawException;
import io.jsonwebtoken.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
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
    @Value("${apple.key_id}")
    private String APPLE_KEY_ID;
    @Value("${apple.team_id}")
    private String APPLE_TEAM_ID;
    @Value("${apple.bundle_id}")
    private String APPLE_BUNDLE_ID;
    @Value("${apple.p8_key_name}")
    private String APPLE_P8_KEY_NAME; // apple에서 다운받은 p8 인증서(resources에 위치)
    final static public long GMT_TIME_FORMATTER_IN_MILLISECONDS = 1000L*60*60*9;   // 9시간(우리나라 표준시와 GMT의 시간 차이)
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
        List<String> findInfo = redisService.getListValue(refreshToken);    // 0 = providerUserId, 1 = refreshToken
        if (findInfo.get(0) == null) { // 유저 정보가 없으면 FAILED 반환
            return new CustomerRefreshResultDto(FAIL_STRING, "No user found", null, null, null);
        }
        if (validateToken(refreshToken))  // refresh Token 유효성 검증 완료 시
        {
            UserDetails findUser = customUserDetailsService.loadUserByProviderUserId((String)findInfo.get(0));
            List<String> roles = findUser.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());
            String newAccessToken = generateAccessToken((String)findInfo.get(0), roles);
            String newRefreshToken = generateRefreshToken();
            return new CustomerRefreshResultDto(SUCCESS_STRING, "Access token refreshed", findInfo.get(0), newAccessToken, newRefreshToken);
        }
        return new CustomerRefreshResultDto(FAIL_STRING, "Refresh token expired", null, null, null);  // refresh Token 만료 시
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

    // <----------------- Apple 연동해제 part ----------------->

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        ClassPathResource resource = new ClassPathResource(APPLE_P8_KEY_NAME);
        InputStream inputStream = resource.getInputStream();    // 배포 환경에서 jar로 실행 시, 압축 과정에서 uri에 jar~이 붙어 getURI()를 통한 파일 읽기가 안됨.
        byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
        String privateKey = new String(bdata, StandardCharsets.UTF_8);
        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

        return converter.getPrivateKey(object);
    }

    public String generateAppleClientSecret() {
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", APPLE_KEY_ID);
        jwtHeader.put("alg", "ES256");
        Date localTime = new Date();    // 한국기준 현재 시간
        Date nowInGMT = new Date(localTime.getTime() - GMT_TIME_FORMATTER_IN_MILLISECONDS); // 한국 시간(사용하는 머신의 리전마다 다를 것임, 우리는 서울 리전의 머신을 쓰는 중) - 9시간 = GMT
        String appleClientSecret = null;
        Date validity = new Date(nowInGMT.getTime() + APPLE_CLIENT_SECRET_VALIDITY_IN_MILLISECONDS);
        try {
            appleClientSecret = Jwts.builder()   // Refresh token 생성
                    .setHeaderParams(jwtHeader)
                    .setIssuer(APPLE_TEAM_ID)
                    .setIssuedAt(nowInGMT) // 발행 시간 - UNIX 시간
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

    private HttpRequest.BodyPublisher getParamsUrlEncoded(Map<String, String> parameters) {
        String urlEncoded = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return HttpRequest.BodyPublishers.ofString(urlEncoded);
    }

    public String getRefreshToken(String clientSecret, String authCode) {

        String refreshToken = "";

        String uriStr = "https://appleid.apple.com/auth/token";

        Map<String, String> params = new HashMap<>();
        params.put("client_secret", clientSecret); // 생성한 clientSecret
        params.put("code", authCode); // 애플 로그인 시, 응답값으로 받은 authrizationCode
        params.put("grant_type", "authorization_code");
        params.put("client_id", "com.mytest.app"); // app bundle id

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .POST(getParamsUrlEncoded(params))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            JSONParser parser = new JSONParser();
            JSONObject jsonObjP = (JSONObject) parser.parse(getResponse.body());    // 애플에서 준 리스폰스 바디를 json 객체화
            refreshToken = jsonObjP.get("refresh_token").toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return refreshToken; // 생성된 refreshToken
    }

    public void withDrawApple(String clientSecret, String refreshToken) throws AppleWithdrawException {   // 애플 회원탈퇴 함수
        String uriStr = "https://appleid.apple.com/auth/revoke";

        Map<String, String> params = new HashMap<>();
        params.put("client_secret", clientSecret); // 생성한 client_secret
        params.put("token", refreshToken); // 생성한 refresh_token
        params.put("client_id", "com.mytest.app"); // app bundle id

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .POST(getParamsUrlEncoded(params))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            int getResponseCode = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString()).statusCode();
            if (getResponseCode == 400) throw new AppleWithdrawException();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
