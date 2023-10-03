package FCM.dto;

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
    public FCMAlertDto(String targetToken, String title, String body) {
        this.targetToken = targetToken;
        this.title = title;
        this.body = body;
    }

}
