package zupzup.back_end.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;
import zupzup.back_end.reservation.dto.OrderDto;
import zupzup.back_end.reservation.service.OrderServiceImpl;

import java.util.List;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderServiceImpl orderServiceImpl;

    // <-------------------- GET part -------------------->
    @GetMapping("")  // order에 대한 GET(주문 항목 모두)
    public List<OrderDto.GetOrderDto> getAllOrderList() throws Exception {
        List<OrderDto.GetOrderDto> allOrderListDto = orderServiceImpl.getAllOrder();

        System.out.print("In Controller ");  // for test
        System.out.println(allOrderListDto);

        return allOrderListDto; // order들의 dto list 반환
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET
    public OrderDto.GetOrderSpecificDto getOrder(@PathVariable Long orderId) throws Exception {
        OrderDto.GetOrderSpecificDto getOrderSpecificDto = orderServiceImpl.getOrderById(orderId);

        System.out.print("In Controller ");  // for test
        System.out.println(getOrderSpecificDto);

        return getOrderSpecificDto; // 한 개 order의 dto 반환
    }
}