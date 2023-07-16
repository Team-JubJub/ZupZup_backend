package com.rest.api.auth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Autowired
    private final StringRedisTemplate stringRedisTemplate;    // String value을 redis에 저장하기 위한 template

    public List<String> getListValue(String refreshToken) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String providerUserId = stringValueOperations.get(refreshToken);
        List<String> findInfo = new ArrayList<>();
        findInfo.add(providerUserId);
        findInfo.add(refreshToken);

        return findInfo;
    }

    public String getStringValue(String accessToken) {  // 현재는 redis에 accessToken 저장돼있는지(로그아웃인지) 판단하는 데 사용.
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        String accessTokenValue = stringValueOperations.get(accessToken);   // log out된 상태라면 "sign-out", 아니라면 null
        if(accessTokenValue == null) {    // access token을 이용해 받은 정보가 없으면(access token을 key로 저장한 providerUserId 없으면) null 리턴
            return null;
        }

        return accessToken;
    }

    public void setStringValue(String token, String data, Long expirationTime) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        // 로그아웃 -> token = accessToken, data = "sign-out" / 로그인 -> token = refreshToken, data = providerUserId
        stringValueOperations.set(token, data, (int) (expirationTime / 1), TimeUnit.MILLISECONDS);

    }

    public void deleteKey(String refreshToken) {
        ValueOperations<String, String> stringValueOperations = stringRedisTemplate.opsForValue();
        stringValueOperations.getAndDelete(refreshToken);   // redis에서 해당 refresh token 데이터 삭제
    }

}
