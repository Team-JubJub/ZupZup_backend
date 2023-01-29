package zupzup.back_end.reservation.dto;

import lombok.Getter;
import lombok.Setter;
import zupzup.back_end.reservation.domain.type.OrderSpecific;
import zupzup.back_end.reservation.domain.type.OrderStatus;

import java.util.List;

public class OrderDto { // DTO 내에  request, response 등을 inner class로 만들어주자.

    // <-------------------- GET part -------------------->
    @Getter
    @Setter
    public static class GetOrderDto { // GET에 mapping할 DTO
        private Long id;    // order ID

        private OrderStatus orderStatus; // 상태여부
        private String username; // 닉네임
        private String orderTitle; // ex) 크로플 3개 외 3
        private String orderTime; // 주문 시간
        private String visitTime; // 방문예정 시간
    }

    @Getter
    @Setter
    public static class GetOrderSpecificDto { // 단건 GET에 mapping할 DTO
        private Long id;    // Order ID

        private OrderStatus orderStatus; // 상태여부
        private String username; // 닉네임
        private String phoneNumber; // 전화번호 -> 필요 없으면 삭제할 것
        private String orderTitle;  // ex) 크로플 3개 외 3개
        private String orderTime;   // 주문 시간
        private String visitTime; // 방문예정 시간
        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }


    // <-------------------- PATCH part -------------------->
    @Getter
    @Setter
    public static class PatchOrderDto {
        private Long id;    // Order ID

        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }
}
