package dto.order.customer.response;

import domain.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

public class OrderResponseDto {

    // <-------------------- GET part -------------------->
    @Getter
    @Setter
    public static class GetOrderDto {
        private String storeName;   // entity에는 store 객체 저장, modelMapper가 알아서 name 빼내줌.
        private OrderStatus orderStatus;
        private String userName; // 닉네임
        private String orderTitle;
        private String orderTime;
        private String visitTime;
    }

}
