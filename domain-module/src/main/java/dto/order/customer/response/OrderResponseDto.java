package dto.order.customer.response;

import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OrderResponseDto {
    // <-------------------- POST part -------------------->
    @Getter
    @Setter
    public static class PostOrderDto {
        private String storeName;   // entity에는 store 객체 저장, modelMapper가 알아서 name 빼내줌.
        private String storeAddress; // 상동
        private OrderStatus orderStatus;
        private String userName; // 닉네임
        private String phoneNumber;
        private String orderTime;
        private String visitTime;
        private List<OrderSpecific> orderList;
        private String message;
        private String href;
    }

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

    @Getter
    @Setter
    public static class GetOrderDetailsDto {
        private String storeName;   // entity에는 store 객체 저장, modelMapper가 알아서 name 빼내줌.
        private String storeAddress; // 상동
        private OrderStatus orderStatus;
        private String userName; // 닉네임
        private String phoneNumber;
        private String orderTime;
        private String visitTime;
        private List<OrderSpecific> orderList;
    }

}
