package com.rest.api.order.domain;

import com.rest.api.order.domain.type.OrderSpecific;
import com.rest.api.order.domain.type.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import com.rest.api.order.dto.OrderServiceDto;
import com.rest.api.store.domain.Store;

import java.util.List;

@Entity
@Getter
@Table(name = "orders")  // table name이 order -> SQL 예약어와 동일, table 명 수정.
public class Order {

    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Store store;    // store (relation with store table)

    private OrderStatus orderStatus; // 상태여부 -> ERD에 추가
    private String username; // 예약자명
    private String phoneNumber; // 예약자 전화번호
    private String orderTitle; // ex) 크로플 3개 외 3
    private String orderTime; // 주문 시간 -> ERD에 추가
    private String visitTime; // 방문예정 시간

    @ElementCollection
    @CollectionTable(name="orderSpecific")
    private List<OrderSpecific> orderList;  // 주문 품목(이름, 가격, 개수, (img)

    public void updateWhenPatch(OrderServiceDto orderServiceDto) {
        this.orderStatus = orderServiceDto.getOrderStatus();
        this.orderList = orderServiceDto.getOrderList();
    }

}
