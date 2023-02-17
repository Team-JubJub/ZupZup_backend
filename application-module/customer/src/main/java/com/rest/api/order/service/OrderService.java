package com.rest.api.order.service;

import domain.order.Order;
import dto.order.OrderDto;
import dto.order.customer.request.OrderRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import repository.OrderRepository;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class OrderService {

    private OrderRepository orderRepository;

    // <-------------------- POST part -------------------->
    public String addOrder(OrderRequestDto.PostOrderDto postOrderDto) {
        OrderDto orderDto = new OrderDto();
        orderDto.setUsername(postOrderDto.getUsername());
        orderDto.setPhoneNumber(postOrderDto.getPhoneNumber());
        orderDto.setVisitTime(postOrderDto.getVisitTime());
        orderDto.setOrderList(postOrderDto.getOrderList());

        Order orderEntity = Order.builder(


        )

        return "주문이 완료되었습니다.";
    }
}
