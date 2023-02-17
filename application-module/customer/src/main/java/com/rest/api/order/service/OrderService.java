package com.rest.api.order.service;

import domain.order.Order;
import dto.order.OrderDto;
import dto.order.customer.request.OrderRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import repository.OrderRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class OrderService {

    private OrderRepository orderRepository;

    // <-------------------- POST part -------------------->
    public String addOrder(OrderRequestDto.PostOrderDto postOrderDto) {
        LocalTime nowTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formattedNowTime = nowTime.format(formatter);

        OrderDto orderDto = new OrderDto();
        orderDto.setUsername(postOrderDto.getUsername());
        orderDto.setPhoneNumber(postOrderDto.getPhoneNumber());
        orderDto.setOrderTime(formattedNowTime);
        orderDto.setVisitTime(postOrderDto.getVisitTime());
        orderDto.setOrderList(postOrderDto.getOrderList());

        Order orderEntity = new Order();
        orderEntity.addOrder(orderDto);
        orderRepository.save(orderEntity);

        return "주문이 완료되었습니다.";
    }
}
