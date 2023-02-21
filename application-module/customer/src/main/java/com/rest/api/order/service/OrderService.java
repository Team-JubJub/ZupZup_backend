package com.rest.api.order.service;

import domain.order.Order;
import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import dto.order.OrderDto;
import dto.order.customer.request.OrderRequestDto;
import dto.order.customer.response.OrderResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.OrderRepository;
import repository.StoreRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class OrderService {

    @Autowired
    ModelMapper modelMapper;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;

    // <-------------------- POST part -------------------->
    public String addOrder(Long storeId, OrderRequestDto.PostOrderDto postOrderDto) {
        String formattedOrderTime = orderTimeSetter();
        OrderDto orderDto = postOrderDTOtoOrderDTO(storeId, postOrderDto, formattedOrderTime);

        Order orderEntity = new Order();
        orderEntity.addOrder(orderDto);
        orderRepository.save(orderEntity);

        return "주문이 완료되었습니다.";
    }

    // <-------------------- GET part -------------------->
    public List<OrderResponseDto.GetOrderDto> orderList() {
        List<Order> allOrderListEntity = orderRepository.findAll();
        List<OrderResponseDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()
            .map(m -> modelMapper.map(m, OrderResponseDto.GetOrderDto.class))
            .collect(Collectors.toList());

        return allOrderListDto;
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    // <--- Methods for readability --->
    private String orderTimeSetter() {
        LocalTime nowTime = LocalTime.now();    // 주문한 시간
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.ENGLISH);   // 09:43 AM, 04:57 PM
        String formattedOrderTime = nowTime.format(formatter);

        return formattedOrderTime;
    }

    private OrderDto postOrderDTOtoOrderDTO(Long storeId, OrderRequestDto.PostOrderDto postOrderDto, String formattedNowTime) {
        Store store = storeRepository.findById(storeId).get();
        OrderSpecific firstAtOrderSpecific = postOrderDto.getOrderList().get(0);
        String firstAtOrderList = firstAtOrderSpecific.getItemName();
        int firstAtOrderListCount = firstAtOrderSpecific.getItemCount();
        int orderListCount = postOrderDto.getOrderList().size() - 1;

        OrderDto orderDto = new OrderDto();
        orderDto.setStore(store);
        orderDto.setOrderStatus(OrderStatus.NEW);
        orderDto.setUserName(postOrderDto.getUserName());
        orderDto.setPhoneNumber(postOrderDto.getPhoneNumber());
        orderDto.setOrderTitle(firstAtOrderList + " " + firstAtOrderListCount + "개 외 " + orderListCount + "건");    // 크로플 3개 외 4건
        orderDto.setOrderTime(formattedNowTime);
        orderDto.setVisitTime(postOrderDto.getVisitTime());
        orderDto.setOrderList(postOrderDto.getOrderList());

        return orderDto;
    }
}
