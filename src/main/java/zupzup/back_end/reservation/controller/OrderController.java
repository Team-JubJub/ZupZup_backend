package zupzup.back_end.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.dto.OrderDto;
import zupzup.back_end.reservation.service.OrderService;
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
    public List<OrderDto> getAllOrderList() throws Exception {
        List<Order> allOrderList = orderServiceImpl.getAllOrder();
        List<OrderDto> allOrderListDto = allOrderList.stream()   // Entity -> Dto
                .map(m -> new OrderDto())
                .collect(Collectors.toList());

        return allOrderListDto;
    }

    @GetMapping("/{reservId}")  // 각 order에 대한 단건 GET
    public void getOrder(@PathVariable Long reservId) throws Exception {
        // return Order상세정보(items with number, price etc...)
    }
}