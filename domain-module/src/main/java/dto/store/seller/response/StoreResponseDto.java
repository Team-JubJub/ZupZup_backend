package dto.store.seller.response;

import lombok.Getter;
import lombok.Setter;

public class StoreResponseDto {

    @Getter
    @Setter
    public static class GetStoreDetailsDto {
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

    @Getter @Setter
    public static class response {

        private String storeName;
        private String openTime;
        private String endTime;

        private String saleMatters;

        private String saleTimeStart;
        private String saleTimeEnd;

        public String closedDay;
    }
}
