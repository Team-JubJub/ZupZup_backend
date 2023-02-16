package dto.store.customer.response;

import lombok.Getter;
import lombok.Setter;

public class StoreResponseDto {

    // <-------------------- GET part -------------------->
    @Getter
    @Setter
    public static class GetStoreDto {
        private Long storeId;

        private String storeName;
        private String category; // ex) 카페 / domain에 column 추가할 것.
        private String saleTimeStart;
        private String saleTimeEnd;
        private String salePercent;    // "00%"
    }
}
