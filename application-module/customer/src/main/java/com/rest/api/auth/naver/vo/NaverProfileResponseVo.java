package com.rest.api.auth.naver.vo;

import lombok.Getter;

@Getter
public class NaverProfileResponseVo {

    private String resultcode;  // API 호출 결과 코드
    private String message; // 호출 결과 메시지
    private NaverProfileVo response;    // Profile 상세 -> 네이버에서의 response가 response: { id: }와 같은 형태로 오기 때문에 이름이 response임.

}
