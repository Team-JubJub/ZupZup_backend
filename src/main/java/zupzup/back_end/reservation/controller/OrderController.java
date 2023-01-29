package zupzup.back_end.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;
import zupzup.back_end.reservation.dto.OrderDto;
import zupzup.back_end.reservation.service.OrderService;

import java.util.List;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/{storeId}/order")
public class OrderController {
    private final OrderService orderServiceImpl;

    // <-------------------- GET part -------------------->
    @GetMapping("")  // order에 대한 GET(주문 항목 모두)
    public List<OrderDto.GetOrderDto> getAllOrderList(@PathVariable Long storeId) {
        List<OrderDto.GetOrderDto> allOrderListDto = orderServiceImpl.getAllOrder(storeId);

        return allOrderListDto; // order들의 dto list 반환
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET
    public OrderDto.GetOrderSpecificDto getOrder(@PathVariable Long storeId, @PathVariable Long orderId) {
        OrderDto.GetOrderSpecificDto getOrderSpecificDto = orderServiceImpl.getOrderById(storeId, orderId);

        return getOrderSpecificDto; // 한 개 order의 dto 반환
    }

    // <-------------------- PATCH part -------------------->
    @PatchMapping("/{orderId}")  // 각 order에 대해 사장님이 주문 확정시 사용할 request
    public OrderDto.GetOrderSpecificDto patchOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody OrderDto.PatchOrderDto patchOrderDto) {
        OrderDto.GetOrderSpecificDto patchedOrderSpecificDto = orderServiceImpl.patchOrderById(storeId, orderId, patchOrderDto);

        return patchedOrderSpecificDto; // patch 된 order의 dto 반환
    }
}