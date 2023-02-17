package com.rest.api.order.controller;

import com.rest.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log
@RequiredArgsConstructor
@RequestMapping("/customer/order")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- POST part -------------------->
    @PostMapping("")
    public ResponseEntity makeOrder() {


        return new ResponseEntity("주문이 완료되었습니다.", HttpStatus.CREATED);
    }
}
