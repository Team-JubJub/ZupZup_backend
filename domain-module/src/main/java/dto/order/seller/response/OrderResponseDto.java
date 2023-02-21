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
        private Long id;    // order ID

        private OrderStatus sellerOrderStatus; // 상태여부
        private String username; // 닉네임
        private String orderTitle; // ex) 크로플 3개 외 3
        private String orderTime; // 주문 시간
        private String visitTime; // 방문예정 시간
    }

    @Getter
    @Setter
    public static class GetOrderDetailsDto { // 단건 GET에 mapping할 DTO
        private Long id;    // Order ID

        private OrderStatus sellerOrderStatus; // 상태여부
        private String username; // 닉네임
        private String phoneNumber; // 전화번호 -> 필요 없으면 삭제할 것
        private String orderTitle;  // ex) 크로플 3개 외 3개
        private String orderTime;   // 주문 시간
        private String visitTime; // 방문예정 시간
        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }

}
