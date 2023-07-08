package dto.order.seller.response;

import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OrderResponseDto {

    // <-------------------- GET part -------------------->
    @Getter
    @Setter
    public static class GetOrderDto { // GET에 mapping할 DTO
        private Long orderId;    // order ID
        private Long storeId;
        private Long userId;
        private OrderStatus orderStatus;
        private String userName; // 닉네임
        private String phoneNumber;
        private String orderTitle; // ex) 크로플 3개 외 3
        private String orderTime; // 주문 시간
        private String visitTime; // 방문예정 시간
        private String storeName;
        private String storeAddress;
        private String category;
        private List<OrderSpecific> orderList;
    }

    @Getter
    @Setter
    public static class GetOrderDetailsDto { // 단건 GET에 mapping할 DTO
        private Long id;    // Order ID
        private OrderStatus orderStatus;
        private String userName; // 닉네임
        private String phoneNumber; // 전화번호 -> 필요 없으면 삭제할 것
        private String orderTitle;  // ex) 크로플 3개 외 3개
        private String orderTime;   // 주문 시간
        private String visitTime; // 방문예정 시간
        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }

    // <-------------------- PATCH part -------------------->
    @Getter
    @Setter
    public static class PatchOrderResponseDto { // PATCH 시 return 할 DTO
        private GetOrderDetailsDto data;
        private String href;
        private String message;
    }

}
