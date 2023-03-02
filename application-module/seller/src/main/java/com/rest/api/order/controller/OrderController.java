package com.rest.api.order.controller;

import dto.order.seller.request.OrderRequestDto;
import dto.order.seller.response.OrderResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.rest.api.order.service.OrderService;

import java.util.List;

@RestController
@Validated
@Log
@RequiredArgsConstructor
@RequestMapping("/seller/{storeId}/order")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- GET part -------------------->
    @GetMapping("")  // order에 대한 GET(주문 항목 모두)
    public ResponseEntity orderList(@PathVariable Long storeId) { // ResponseEntity의 type이 뭐가될지 몰라서 우선 Type 지정 없이 둠.
        List<OrderResponseDto.GetOrderDto> allOrderListDto = orderService.orderList(storeId);
        if(allOrderListDto.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT); // NO_CONTENT 시 body가 빈 상태로 감. 204
        }

        return new ResponseEntity(allOrderListDto, HttpStatus.OK); // order들의 dto list 반환, 200
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET
    public ResponseEntity orderDetails(@PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.GetOrderDetailsDto getOrderDetailsDto = orderService.orderDetails(storeId, orderId);

        return new ResponseEntity(getOrderDetailsDto, HttpStatus.OK); // 한 개 order의 dto 반환
    }

    // <-------------------- PATCH part -------------------->
    @PatchMapping("/{orderId}")  // 각 order에 대해 사장님이 주문 확정시 사용할 request
    public ResponseEntity updateOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody @Valid OrderRequestDto.PatchOrderDto patchOrderDto) {
        String response = orderService.updateOrder(storeId, orderId, patchOrderDto);

        return new ResponseEntity(response, HttpStatus.OK); // patch 된 order의 dto 반환
    }

}