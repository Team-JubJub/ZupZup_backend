package com.rest.api.auth.redis;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

//    @Autowired
//    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private final StringRedisTemplate stringRedisTemplate;    // String value을 redis에 저장하기 위한 template

    @Transactional
    public List<String> getListValue(String refreshToken) {
//        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
//        String getRefreshToken = refreshTokenEntity.getRefreshToken();
//        String getProviderUserId = refreshTokenEntity.getProviderUserId();
//        List<String> findInfo = new ArrayList<>();
//        findInfo.add(getProviderUserId);
//        findInfo.add(getRefreshToken);
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String providerUserId = stringValueOperations.get(refreshToken);
        List<String> findInfo = new ArrayList<>();
        findInfo.add(providerUserId);
        findInfo.add(refreshToken);

        return findInfo;
    }

    @Transactional
    public String getAccessTokenValue(String accessToken) {  // 현재는 redis에 accessToken 저장돼있는지(로그아웃인지) 판단하는 데 사용.
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String accessTokenValue = stringValueOperations.get(accessToken);   // log out된 상태라면 "sign-out", 아니라면 null
        if(accessTokenValue == null) {    // access token을 이용해 받은 정보가 없으면(access token을 key로 저장한 providerUserId 없으면) null 리턴
            return null;
        }

        return accessToken;
    }

    @Transactional
    public void setStringValue(String token, String data, Long expirationTime) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.set(token, data, (int) (expirationTime / 1));
        // 로그아웃 -> token = accessToken, data = "sign-out" / 로그인 -> token = refreshToken, data = providerUserId
//        if(data.equals("sign-out")) {    // 로그아웃 상황에 data를 "sign-out"로 줌.
////            String accessToken = token;
////            Long remainExpirationTime = expirationTime; // 로그아웃 시 access token의 남은 유효시간
////            LocalDateTime expiredAt = LocalDateTime.now().plusSeconds((int) (expirationTime / 1000));  // 밀리초 단위이므로 나누기 100해줌
////            System.out.println(expiredAt);  // For test
////            Integer expiration = (int) (expirationTime / 1);
////
////            refreshTokenRepository.save(RefreshToken.builder()
////                    .accessToken(accessToken)
////                    .expiration(expiration)
////                    .build());
//            stringValueOperations.set(token, data, (int) (expirationTime / 1));
//        }
//        else {  // 로그인 상황에 data는 providerUserId
////            String refreshToken = token;
////            String providerUserId = data;
////            LocalDateTime expiredAt = LocalDateTime.now().plusSeconds((int) (expirationTime / 1000));
////            System.out.println(expiredAt);  // For test
////            Integer expiration = (int) (expirationTime / 1);
////
////            refreshTokenRepository.save(RefreshToken.builder()
////                    .refreshToken(refreshToken)
////                    .providerUserId(providerUserId)
////                    .expiration(expiration)
////                    .build());
//            stringValueOperations.set(token, data, (int) (expirationTime / 1));
//        }
    }

    @Transactional
    public void deleteToken(String refreshToken) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.getAndDelete(refreshToken);   // redis에서 해당 refresh token 데이터 삭제
//        RefreshToken refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
    }

}
