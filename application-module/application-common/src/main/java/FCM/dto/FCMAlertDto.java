package FCM.dto;

import lombok.Builder;

public class FCMAlertDto {

    private Long targetId;
    private String title;
    private String body;

    @Builder
    public FCMAlertDto(Long targetId, String title, String body) {
        this.targetId = targetId;
        this.title = title;
        this.body = body;
    }

}
