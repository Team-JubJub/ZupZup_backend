package dto.order.customer.response;

import lombok.Getter;
import lombok.Setter;

public class OrderResponseDto {

    // <-------------------- GET part -------------------->
    @Getter
    @Setter
    public static class GetOrderDto {
        private String storeName;
        private String orderStatus;
        private String username; // 닉네임
        private String orderTitle;
        private String orderTime;
        private String visitTime;
    }

}
