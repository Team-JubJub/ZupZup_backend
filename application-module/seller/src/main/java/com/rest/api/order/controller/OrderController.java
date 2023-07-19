package com.rest.api.order.controller;

import dto.order.seller.request.OrderRequestDto;
import dto.order.seller.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.rest.api.order.service.OrderService;

@RestController
@Validated
@Log
@RequiredArgsConstructor
@RequestMapping("/seller/{storeId}/order")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- GET part -------------------->
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 조회 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.GetOrderListDto.class))),
            @ApiResponse(responseCode = "204", description = "주문 목록 없음")
    })
    @GetMapping("")  // order에 대한 GET(주문 항목 모두), ex) ~/seller/1/order?page=1 포맷
    public ResponseEntity orderList(@PathVariable Long storeId, @PageableDefault(size=10, sort="orderId", direction=Sort.Direction.DESC) Pageable pageable) { // ResponseEntity의 type이 뭐가될지 몰라서 우선 Type 지정 없이 둠.
        int page = pageable.getPageNumber();
        OrderResponseDto.GetOrderListDto getOrderListDto = orderService.orderList(storeId, page, pageable);
        if(getOrderListDto.getOrderList().size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT); // NO_CONTENT 시 body가 빈 상태로 감. 204
        }

        return new ResponseEntity(getOrderListDto, HttpStatus.OK); // order들의 dto list 반환, 200
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET    -> 일단 안쓰일 듯
    public ResponseEntity orderDetails(@PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.GetOrderDetailsDto getOrderDetailsDto = orderService.orderDetails(storeId, orderId);

        return new ResponseEntity(getOrderDetailsDto, HttpStatus.OK); // 한 개 order의 dto 반환
    }

    // <-------------------- PATCH part -------------------->
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "주문 확정 시점에 DB상 재고보다 많은 양의 주문일 경우",
                    content = @Content(schema = @Schema(example = "주문 중 상품의 재고가 주문 확정한 개수보다 부족합니다. 상품 명(ID): ")))
    })
    @PatchMapping("/new-order/{orderId}/cancel")  // 신규 주문 취소 시
    public ResponseEntity updateOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody @Valid OrderRequestDto.PatchOrderDto patchOrderDto) {
        OrderResponseDto.PatchOrderResponseDto patchOrderResponseDto = orderService.confirmOrder(storeId, orderId, patchOrderDto);

        return new ResponseEntity(patchOrderResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
    }

//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "주문 확정 성공",
//                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class)))
//    })
//    @PatchMapping("/new-order/{orderId}/confirm")  // 신규 주문 확정 시
//    public ResponseEntity updateOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody @Valid OrderRequestDto.PatchOrderDto patchOrderDto) {
//        OrderResponseDto.PatchOrderResponseDto patchOrderResponseDto = orderService.confirmOrder(storeId, orderId, patchOrderDto);
//
//        return new ResponseEntity(patchOrderResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
//    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class)))
    })
    @PatchMapping("/confirmed-order/{orderId}/cancel")    // 확정 주문 취소 시
    public ResponseEntity completeOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody @Valid OrderRequestDto.PatchOrderDto patchOrderDto) {
        OrderResponseDto.PatchOrderResponseDto completeOrderDto = orderService.completeOrder(storeId, orderId, patchOrderDto);

        return new ResponseEntity(completeOrderDto, HttpStatus.OK);
    }

//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "주문 완료 성공",
//                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class)))
//    })
//    @PatchMapping("/confirmed-order/{orderId}/complete")  // 확정 주문 완료 시
//    public ResponseEntity updateOrder(@PathVariable Long storeId, @PathVariable Long orderId, @RequestBody @Valid OrderRequestDto.PatchOrderDto patchOrderDto) {
//        OrderResponseDto.PatchOrderResponseDto patchOrderResponseDto = orderService.confirmOrder(storeId, orderId, patchOrderDto);
//
//        return new ResponseEntity(patchOrderResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
//    }

}