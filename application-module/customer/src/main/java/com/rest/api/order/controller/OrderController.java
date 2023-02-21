package com.rest.api.order.controller;

import com.rest.api.order.service.OrderService;
import dto.order.customer.request.OrderRequestDto;
import dto.order.customer.response.OrderResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/customer")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- POST part -------------------->
    @PostMapping("/store/{storeId}")    // end point에 order 추가돼야할지 고민할 것
    public ResponseEntity addOrder(@PathVariable Long storeId, @RequestBody @Valid OrderRequestDto.PostOrderDto postOrderDto) {
        String addOrderResult = orderService.addOrder(storeId, postOrderDto);

        return new ResponseEntity(addOrderResult, HttpStatus.CREATED);
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

    @GetMapping("/order/{order_id}")
    public ResponseEntity orderDetails(@PathVariable Long orderId) {

        return new ResponseEntity(HttpStatus.OK);
    }

}
