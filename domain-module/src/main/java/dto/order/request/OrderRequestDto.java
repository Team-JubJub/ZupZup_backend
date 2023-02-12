package dto.order.request;

import domain.order.type.OrderSpecific;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class OrderRequestDto {
    // <-------------------- PATCH part -------------------->
    @Getter
    @Setter
    public static class PatchOrderDto {
        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }

}