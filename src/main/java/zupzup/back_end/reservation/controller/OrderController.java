package zupzup.back_end.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.dto.OrderDto;
import zupzup.back_end.reservation.service.OrderServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderServiceImpl orderServiceImpl;

    @GetMapping("")  // order에 대한 GET(주문 항목 모두)
    public List<OrderDto.GetOrderDto> getAllOrderList() throws Exception {
        List<Order> allOrderList = orderServiceImpl.getAllOrder();
        List<OrderDto.GetOrderDto> allOrderListDto = allOrderList.stream()   // Entity -> Dto
                .map(m -> new OrderDto.GetOrderDto())
                .collect(Collectors.toList());

        return allOrderListDto; // order들의 dto list 반환
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET
    public OrderDto.GetOrderSpecificDto getOrder(@PathVariable Long orderId) throws Exception {
        Order order = orderServiceImpl.getOrderById(orderId);
        OrderDto.GetOrderSpecificDto getOrderSpecificDto = new OrderDto.GetOrderSpecificDto();
        getOrderSpecificDto.of(order);

        return getOrderSpecificDto; // 한 개 order의 dto 반환
    }
}