package zupzup.back_end.reservation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;
import zupzup.back_end.reservation.dto.OrderRequestDto;
import zupzup.back_end.reservation.service.OrderService;

import java.util.List;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/{storeId}/order")
public class OrderController {
    private final OrderService orderService;

    // <-------------------- GET part -------------------->
    @GetMapping("")  // order에 대한 GET(주문 항목 모두)
    public List<OrderRequestDto.GetOrderDto> getAllOrderList(@PathVariable Long storeId) {
        List<OrderRequestDto.GetOrderDto> allOrderListDto = orderService.getAllOrder(storeId);

        return allOrderListDto; // order들의 dto list 반환
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET
    public OrderRequestDto.GetOrderSpecificDto getOrder(@PathVariable Long storeId, @PathVariable Long orderId) {
        OrderRequestDto.GetOrderSpecificDto getOrderSpecificDto = orderService.getOrderById(storeId, orderId);

        return getOrderSpecificDto; // 한 개 order의 dto 반환
    }

    // <-------------------- PATCH part -------------------->
    @PatchMapping("/{orderId}")  // 각 order에 대해 사장님이 주문 확정시 사용할 request
    public OrderRequestDto.GetOrderSpecificDto patchOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody OrderRequestDto.PatchOrderDto patchOrderDto) {
        OrderRequestDto.GetOrderSpecificDto patchedOrderSpecificDto = orderService.patchOrderById(storeId, orderId, patchOrderDto);

        return patchedOrderSpecificDto; // patch 된 order의 dto 반환
    }
}