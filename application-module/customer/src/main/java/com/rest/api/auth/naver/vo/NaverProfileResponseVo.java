package com.rest.api.auth.naver.vo;

import lombok.Getter;

@Getter
public class NaverProfileResponseVo {

    private String resultCode;  // API 호출 결과 코드
    private String message; // 호출 결과 메시지
    private NaverProfileVo naverProfileVo;    // Profile 상세

}
