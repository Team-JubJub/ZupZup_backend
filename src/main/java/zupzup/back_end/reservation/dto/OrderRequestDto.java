package zupzup.back_end.reservation.dto;

import lombok.Getter;
import lombok.Setter;
import zupzup.back_end.reservation.domain.type.OrderSpecific;

import java.util.List;

public class OrderRequestDto {
    // <-------------------- PATCH part -------------------->
    @Getter
    @Setter
    public static class PatchOrderDto {
        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }

}
