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
    public static class GetOrderListDto {
        private List<GetOrderDetailsDto> orderList;
        private int pageNo;
        private Boolean hasNext;
    }

    @Getter
    @Setter
    public static class GetOrderDetailsDto { // 단건 GET에 mapping할 DTO
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

    // <-------------------- PATCH part -------------------->
    @Getter
    @Setter
    public static class PatchOrderDataResponseDto { // PATCH 시 return 할 DTO(신규 주문 확정, 확정 주문 취소 시에만 사용)
        private GetOrderDetailsDto data;
        private String message;
    }

    @Getter
    @Setter
    public static class PatchOrderStatusResponseDto {
        private GetOrderDetailsDto data;
        private String message;
    }

}
