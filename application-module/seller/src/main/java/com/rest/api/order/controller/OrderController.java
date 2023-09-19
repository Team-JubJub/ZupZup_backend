package com.rest.api.order.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import domain.order.type.OrderStatus;
import dto.order.seller.request.PatchOrderDataDto;
import dto.order.seller.response.GetOrderDetailsDto;
import dto.order.seller.response.GetOrderListDto;
import dto.order.seller.response.PatchOrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.rest.api.order.service.OrderService;

@Tag(name = "Order", description = "주문과 관련된 API")
@RestController
@Validated
@Log
@RequiredArgsConstructor
@RequestMapping("/seller/{storeId}/order")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- GET part -------------------->
    @Operation(summary = "가게의 주문 정보 반환", description = "가게의 주문 정보 반환 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 조회 성공",
                    content = @Content(schema = @Schema(implementation = dto.order.seller.response.GetOrderListDto.class))),
            @ApiResponse(responseCode = "204", description = "주문 정보 요청은 성공했으나 해당 가게의 주문이 0개인 경우"),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user.")))
    })
    @GetMapping("")  // order에 대한 GET(주문 항목 모두), ex) ~/seller/1/order?page=1 포맷
    public ResponseEntity orderList(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                    @PathVariable Long storeId,
                                    @Parameter(name = "deviceToken", description = "가게 운영자의 device token", in = ParameterIn.QUERY) @RequestParam String deviceToken) { // ResponseEntity의 type이 뭐가될지 몰라서 우선 Type 지정 없이 둠.
        GetOrderListDto getOrderListDto = orderService.orderList(storeId, deviceToken);
        if(getOrderListDto.getOrders().size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT); // NO_CONTENT 시 body가 빈 상태로 감. 204
        }

        return new ResponseEntity(getOrderListDto, HttpStatus.OK); // order들의 dto list 반환, 200
    }

    @Operation(summary = "주문의 상세 정보 반환", description = "주문의 상세 정보 반환 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문의 상세 정보 반환 성공",
                    content = @Content(schema = @Schema(implementation = dto.order.seller.response.GetOrderDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user."))),
            @ApiResponse(responseCode = "404", description = "해당 주문을 찾을 수 없음",
                    content = @Content(schema = @Schema(example = "해당 주문을 찾을 수 없습니다.")))
    })
    @GetMapping("/{orderId}")  // 각 order에 대한 단건 GET    -> 일단 안쓰일 듯
    public ResponseEntity orderDetails(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                       @PathVariable Long storeId, @PathVariable Long orderId) {
        GetOrderDetailsDto getOrderDetailsDto = orderService.orderDetails(storeId, orderId);

        return new ResponseEntity(getOrderDetailsDto, HttpStatus.OK); // 한 개 order의 dto 반환
    }

    // <-------------------- PATCH part -------------------->
    @Operation(summary = "신규 주문 취소", description = "신규 주문에 대한 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공",
                    content = @Content(schema = @Schema(implementation = PatchOrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user.")))
    })
    @PatchMapping("/new-order/{orderId}/cancel")  // 신규 주문 취소 시
    public ResponseEntity cancelNewOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                         @PathVariable Long storeId, @PathVariable Long orderId) {
        PatchOrderResponseDto patchOrderStatusResponseDto = orderService.updateOrderStatus(storeId, orderId, OrderStatus.CANCEL);

        return new ResponseEntity(patchOrderStatusResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
    }

    @Operation(summary = "신규 주문 확정", description = "신규 주문에 대한 확정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 확정 성공",
                    content = @Content(schema = @Schema(implementation = PatchOrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user.")))
    })
    @PatchMapping("/new-order/{orderId}/confirm")  // 신규 주문 확정 시
    public ResponseEntity confirmNewOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                          @PathVariable Long storeId, @PathVariable Long orderId, @RequestBody PatchOrderDataDto patchOrderDataDto) {
        PatchOrderResponseDto patchOrderResponseDto = orderService.updateOrderData(storeId, orderId, patchOrderDataDto, OrderStatus.CONFIRM);

        return new ResponseEntity(patchOrderResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
    }

    @Operation(summary = "확정 주문 취소", description = "확정 주문에 대한 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 취소 성공",
                    content = @Content(schema = @Schema(implementation = PatchOrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user.")))
    })
    @PatchMapping("/confirmed-order/{orderId}/cancel")    // 확정 주문 취소 시
    public ResponseEntity cancelConfirmedOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                               @PathVariable Long storeId, @PathVariable Long orderId, @RequestBody PatchOrderDataDto patchOrderDataDto) {
        PatchOrderResponseDto completeOrderDto = orderService.updateOrderData(storeId, orderId, patchOrderDataDto, OrderStatus.CANCEL);

        return new ResponseEntity(completeOrderDto, HttpStatus.OK);
    }

    @Operation(summary = "확정 주문 완료", description = "확정 주문에 대한 완료")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 완료 성공",
                    content = @Content(schema = @Schema(implementation = PatchOrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료, 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n or \n" +
                            "Sign-outed or deleted user.")))
    })
    @PatchMapping("/confirmed-order/{orderId}/complete")  // 확정 주문 완료 시
    public ResponseEntity completeConfirmedOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                                 @PathVariable Long storeId, @PathVariable Long orderId) {
        PatchOrderResponseDto patchOrderStatusResponseDto = orderService.updateOrderStatus(storeId, orderId, OrderStatus.COMPLETE);

        return new ResponseEntity(patchOrderStatusResponseDto, HttpStatus.OK); // patch 된 order의 dto 반환
    }

}