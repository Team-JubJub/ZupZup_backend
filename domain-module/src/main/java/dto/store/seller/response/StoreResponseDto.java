package dto.store.seller.response;

import lombok.Getter;
import lombok.Setter;

public class StoreResponseDto {

    @Getter @Setter
    public static class response {

        private String storeName;
        private String openTime;
        private String endTime;

        private String saleMatters;

        private String saleTimeStart;
        private String saleTimeEnd;
    }
}
