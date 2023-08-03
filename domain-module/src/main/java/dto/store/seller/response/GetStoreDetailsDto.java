package dto.store.seller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetStoreDetailsDto {

    private Long storeId;
    private Long sellerId;
    private String storeName; //가게이름
    private String storeImageUrl;
    private String storeAddress;
    private String category;
    private String contact;
    private Double longitude;
    private Double latitude;
    private String openTime;
    private String closeTime;
    private String saleTimeStart;
    private String saleTimeEnd;
    private String saleMatters; //영업 관련 사항 / ex. 공휴일 휴무
    private Boolean isOpen;
    private String closedDay;

}