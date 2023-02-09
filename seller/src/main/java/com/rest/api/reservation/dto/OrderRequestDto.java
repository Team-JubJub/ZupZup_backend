package com.rest.api.reservation.dto;

import com.rest.api.reservation.domain.type.OrderSpecific;
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
