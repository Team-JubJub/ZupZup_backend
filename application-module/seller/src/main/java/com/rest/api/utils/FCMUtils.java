package com.rest.api.utils;

import com.rest.api.FCM.dto.FCMAlertDto;
import com.rest.api.FCM.service.FCMService;
import com.zupzup.untact.model.domain.auth.user.User;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.repository.StoreRepository;
import com.zupzup.untact.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class FCMUtils {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final FCMService fcmService;

    public void sendMessageToOrderedUser(String deviceToken, String title, String message) {    // 주문 시 푸시 알림 보내는 함수(한 유저에게만)
        FCMAlertDto fcmAlertDto = new FCMAlertDto(deviceToken, title, message);
        String result = fcmService.sendMessage(fcmAlertDto);
        System.out.println("사용자 앱에 주문 관련해서 보낸 알림, 디바이스 토큰 " + deviceToken + " / 결과 : " + result);
    }

    public void sendMessageToAlertUsers(Long storeId, String title, String message) {   // 신규 아이템 등록, 공지사항 등록 시 푸시 알림 보내는 함수(알림 설정한 유저에게)
        Store storeEntity = storeRepository.findById(storeId).get();
        List<Long> alertUsers = new ArrayList<>(storeEntity.getAlertUsers());   // 알림 설정한 유저들 가져오기

        for (int i = 0; i < alertUsers.size(); i++) {
            User userEntity = userRepository.findById(alertUsers.get(i)).get();
            String deviceToken = userEntity.getDeviceToken();
            FCMAlertDto fcmAlertDto = new FCMAlertDto(deviceToken, title, message);
            String result = fcmService.sendMessage(fcmAlertDto);
            System.out.println("사용자 앱에 신규 아이템 등록, 공지사항 등록 관련해서 보낸 알림, 디바이스 토큰 " + deviceToken + " / 결과 : " + result);
        }
    }

}
