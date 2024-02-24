package com.rest.api.FCM.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class NotificationRequest {

    private Message message;

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
    public static class Message {
        private Notification notification;
        private String token;
        private Apns apns;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
    }

}
