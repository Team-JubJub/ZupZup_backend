package com.rest.api.order.dto;

import com.rest.api.order.domain.type.OrderSpecific;
import com.rest.api.order.domain.type.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderServiceDto {
    private Long id;    // Order ID

    private OrderStatus orderStatus; // 상태여부
    private String username; // 닉네임
    private String phoneNumber; // 전화번호 -> 필요 없으면 삭제할 것
    private String orderTitle;  // ex) 크로플 3개 외 3개
    private String orderTime;   // 주문 시간
    private String visitTime; // 방문예정 시간
    private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
}
