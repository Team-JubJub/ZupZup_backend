package zupzup.back_end.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zupzup.back_end.reservation.dto.OrderRequestDto;
import zupzup.back_end.reservation.dto.OrderResponseDto;
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
    public ResponseEntity getAllOrderList(@PathVariable Long storeId) {
        List<OrderResponseDto.GetOrderDto> allOrderListDto = orderService.getAllOrder(storeId);
        if(allOrderListDto.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT); // NO_CONTENT 시 body가 빈 상태로 감.
//            return new ResponseEntity("주문 목록이 비어있습니다.", HttpStatus.OK); // OK로 반환하고 body에 해당 내용 넣어줄지 생각해볼 것.
        }

        return new ResponseEntity(allOrderListDto, HttpStatus.OK); // order들의 dto list 반환
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET
    public ResponseEntity getOrder(@PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.GetOrderSpecificDto getOrderSpecificDto = orderService.getOrderById(storeId, orderId);

        return new ResponseEntity(getOrderSpecificDto, HttpStatus.OK); // 한 개 order의 dto 반환
    }

    // <-------------------- PATCH part -------------------->
    @PatchMapping("/{orderId}")  // 각 order에 대해 사장님이 주문 확정시 사용할 request
    public ResponseEntity patchOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody @Valid OrderRequestDto.PatchOrderDto patchOrderDto) {
        String response = orderService.patchOrderById(storeId, orderId, patchOrderDto);

        return new ResponseEntity(response, HttpStatus.OK); // patch 된 order의 dto 반환
    }
}