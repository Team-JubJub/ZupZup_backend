package dto.order.customer.request;

import domain.order.type.OrderSpecific;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OrderRequestDto {

    // <-------------------- POST part -------------------->
    @Getter
    @Setter
    public static class PostOrderDto {
        private Long storeId;
        private String username; // 닉네임
        private String phoneNumber; // 전화번호 -> 필요 없으면 삭제할 것
        private String visitTime;
        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }

}
