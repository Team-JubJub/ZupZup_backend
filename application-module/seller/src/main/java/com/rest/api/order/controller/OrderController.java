package com.rest.api.order.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import domain.order.type.OrderStatus;
import dto.order.seller.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    public ResponseEntity orderList(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                    @PathVariable Long storeId, @PageableDefault(size=10, sort="orderId", direction=Sort.Direction.DESC) Pageable pageable) { // ResponseEntity의 type이 뭐가될지 몰라서 우선 Type 지정 없이 둠.
        int page = pageable.getPageNumber();
        OrderResponseDto.GetOrderListDto getOrderListDto = orderService.orderList(storeId, page, pageable);
        if(getOrderListDto.getOrderList().size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT); // NO_CONTENT 시 body가 빈 상태로 감. 204
        }

        return new ResponseEntity(getOrderListDto, HttpStatus.OK); // order들의 dto list 반환, 200
    }

    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET    -> 일단 안쓰일 듯
    public ResponseEntity orderDetails(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                       @PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.GetOrderDetailsDto getOrderDetailsDto = orderService.orderDetails(storeId, orderId);

        return new ResponseEntity(getOrderDetailsDto, HttpStatus.OK); // 한 개 order의 dto 반환
    }

    // <-------------------- PATCH part -------------------->
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class)))
    })
    @PatchMapping("/new-order/{orderId}/cancel")  // 신규 주문 취소 시
    public ResponseEntity cancelNewOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.PatchOrderResponseDto patchOrderStatusResponseDto = orderService.updateOrderStatus(storeId, orderId, OrderStatus.CANCEL);

        return new ResponseEntity(patchOrderStatusResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 확정 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class)))
    })
    @PatchMapping("/new-order/{orderId}/confirm")  // 신규 주문 확정 시
    public ResponseEntity confirmNewOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                          @PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.PatchOrderResponseDto patchOrderResponseDto = orderService.updateOrderData(storeId, orderId, OrderStatus.CONFIRM);

        return new ResponseEntity(patchOrderResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class)))
    })
    @PatchMapping("/confirmed-order/{orderId}/cancel")    // 확정 주문 취소 시
    public ResponseEntity cancelConfirmedOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                               @PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.PatchOrderResponseDto completeOrderDto = orderService.updateOrderData(storeId, orderId, OrderStatus.CANCEL);

        return new ResponseEntity(completeOrderDto, HttpStatus.OK);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 완료 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.PatchOrderResponseDto.class)))
    })
    @PatchMapping("/confirmed-order/{orderId}/complete")  // 확정 주문 완료 시
    public ResponseEntity completeConfirmedOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                                 @PathVariable Long storeId, @PathVariable Long orderId) {
        OrderResponseDto.PatchOrderResponseDto patchOrderStatusResponseDto = orderService.updateOrderStatus(storeId, orderId, OrderStatus.COMPLETE);

        return new ResponseEntity(patchOrderStatusResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
    }

}