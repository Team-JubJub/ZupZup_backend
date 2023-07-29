package dto.store.customer.response;

import dto.item.seller.response.ItemResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class StoreResponseDto {

    // <-------------------- GET part -------------------->
    @Getter
    @Setter
    public class GetStoreDto {
        private Long storeId;

        private String storeName;
        private String category; // ex) 카페 / domain에 column 추가할 것.
        private String saleTimeStart;
        private String saleTimeEnd;
        private String salePercent;    // "00%"
    }

    @Getter
    @Setter
    public class GetStoreDetailDto {

        private Long storeId;

        private String storeName;
        private String category;
        private String storeAddress;

        private String openTime;
        private String endTime;
        private String saleMatters;

        private String saleTimeStart;
        private String saleTimeEnd;

        private String salePercent;

        private Double longitude;
        private Double latitude;

        private List<String> eventList;

        private List<ItemResponseDto> itemDtoList;
    }
}
