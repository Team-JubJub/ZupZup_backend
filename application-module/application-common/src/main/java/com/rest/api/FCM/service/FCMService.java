package com.rest.api.FCM.service;

import com.google.firebase.messaging.*;
import com.rest.api.FCM.dto.FCMAlertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;


    public String sendMessage(FCMAlertDto fcmAlertDto) {
        Notification notification = Notification.builder()
                .setTitle(fcmAlertDto.getTitle())
                .setBody(fcmAlertDto.getBody())
                .build();
        Message message = Message.builder()
                .setToken(fcmAlertDto.getTargetToken())
                .setNotification(notification)
                .setApnsConfig(ApnsConfig.builder() // iOS에서 푸시 알림 시 진동, 소리 등을 동반하도록 설정해줌.
                        .setAps(Aps.builder().setSound("default").build()).build())
                .build();
        try {
            firebaseMessaging.send(message);
            return "알림을 성공적으로 보냈습니다.";
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "알림을 성공적으로 보내지 못했습니다.";
        }
    }

}