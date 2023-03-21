package com.rest.api.order.controller;

import com.rest.api.order.service.OrderService;
import dto.order.customer.request.OrderRequestDto;
import dto.order.customer.response.OrderResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@Log
@RequiredArgsConstructor
@RequestMapping("/customer")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- POST part -------------------->
    @PostMapping("/store/{storeId}/order")
    public ResponseEntity addOrder(@PathVariable Long storeId, @RequestBody @Valid OrderRequestDto.PostOrderDto postOrderDto) {
        OrderResponseDto.PostOrderResponseDto postOrderResponseDto = orderService.addOrder(storeId, postOrderDto);

        return new ResponseEntity(postOrderResponseDto, HttpStatus.CREATED);
    }

    // <-------------------- GET part -------------------->
    @GetMapping("/order")
    public ResponseEntity orderList() {
        List<OrderResponseDto.GetOrderDto> allOrderListDto = orderService.orderList();
        if(allOrderListDto.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(allOrderListDto, HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity orderDetails(@PathVariable Long orderId) {
        OrderResponseDto.GetOrderDetailsDto orderDetailsDto = orderService.orderDetails(orderId);

        return new ResponseEntity(orderDetailsDto, HttpStatus.OK);
    }

}
