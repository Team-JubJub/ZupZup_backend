package dto.store.seller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ModifyStoreDto {

    private String storeImageUrl; // 가게 대표 이미지 url - 이미지 없을 시 기본이미지
    private String openTime; // 운영 시작 시간
    private String closeTime; // 운영 마감 시간
    private String saleTimeStart;   // 할인 시작 시간
    private String saleTimeEnd; // 할인 마감 시간
    private String closedDay; // 휴무일

}