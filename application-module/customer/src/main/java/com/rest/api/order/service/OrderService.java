package com.rest.api.order.service;

import domain.order.Order;
import domain.order.type.OrderSpecific;
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
        LocalTime nowTime = LocalTime.now();    // 주문한 시간
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");   // 09:43 am, 04:57 pm
        String formattedNowTime = nowTime.format(formatter);

        OrderSpecific firstAtOrderSpecific = postOrderDto.getOrderList().get(0);    // 주문 제목에 쓰일 주문의 첫 번째 목록
        String firstAtOrderList = firstAtOrderSpecific.getItemName();
        int firstAtOrderListCount = firstAtOrderSpecific.getItemCount();
        int orderListCount = postOrderDto.getOrderList().size() - 1;

        OrderDto orderDto = new OrderDto();
        orderDto.setUsername(postOrderDto.getUsername());
        orderDto.setPhoneNumber(postOrderDto.getPhoneNumber());
        orderDto.setOrderTitle(firstAtOrderList + " " + firstAtOrderListCount + "개 외 " + orderListCount + "건");    // 크로플 3개 외 4건
        orderDto.setOrderTime(formattedNowTime);
        orderDto.setVisitTime(postOrderDto.getVisitTime());
        orderDto.setOrderList(postOrderDto.getOrderList());

        Order orderEntity = new Order();
        orderEntity.addOrder(orderDto);
        orderRepository.save(orderEntity);

        return "주문이 완료되었습니다.";
    }
}
