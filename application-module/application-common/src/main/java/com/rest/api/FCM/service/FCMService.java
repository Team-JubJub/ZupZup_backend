package com.rest.api.FCM.service;

import com.rest.api.FCM.dto.FCMAlertDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
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