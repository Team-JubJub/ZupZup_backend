package com.rest.api.order.controller;

import com.rest.api.order.service.OrderService;
import dto.order.customer.request.OrderRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/customer")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- POST part -------------------->
    @PostMapping("/store/{storeId}")
    public ResponseEntity addOrder(@PathVariable Long storeId, @RequestBody @Valid OrderRequestDto.PostOrderDto postOrderDto) {
        String addOrderResult = orderService.addOrder(storeId, postOrderDto);

        return new ResponseEntity(addOrderResult, HttpStatus.CREATED);
    }
}
