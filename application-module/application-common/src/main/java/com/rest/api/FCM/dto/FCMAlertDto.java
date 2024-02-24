package com.rest.api.FCM.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FCMAlertDto {

    private String targetToken; // 메세지를 보낼 device token
    private String title;   // 메세지 제목
    private String body;    // 메세지 내용

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Apns{
        private Payload payload;

    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Payload{
        private Aps aps;

    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Aps{
        private String sound;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;
        private String token;
        private Apns apns;
    }

    @Builder
    public FCMAlertDto(String targetToken, String title, String body) {
        this.targetToken = targetToken;
        this.title = title;
        this.body = body;
    }

}
