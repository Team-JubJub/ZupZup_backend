package com.rest.api.order.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.order.service.OrderService;
import dto.info.customer.response.GetInfoResponseDto;
import dto.order.customer.request.PostOrderRequestDto;
import dto.order.customer.response.GetOrderDetailsDto;
import dto.order.customer.response.GetOrderDto;
import dto.order.customer.response.PostOrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order", description = "사용자의 주문 정보와 관련된 API")
@RestController
@Validated
@Log
@RequiredArgsConstructor
@RequestMapping("/customer")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- POST part -------------------->
    @PostMapping("/store/{storeId}/order")
    public ResponseEntity addOrder(@PathVariable Long storeId, @RequestBody @Valid PostOrderRequestDto postOrderRequestDto) {
        PostOrderResponseDto postOrderResponseDto = orderService.addOrder(storeId, postOrderRequestDto);

        return new ResponseEntity(postOrderResponseDto, HttpStatus.CREATED);
    }

    // <-------------------- GET part -------------------->
    @Operation(summary = "유저의 주문 정보 반환", description = "유저의 주문 정보 반환 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저의 주문 정보(리스트) 반환 성공",
                    content = @Content(schema = @Schema(implementation = GetOrderDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)"))),
            @ApiResponse(responseCode = "401", description = "로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @GetMapping("/order")
    public ResponseEntity orderList(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken) {
        List<GetOrderDto> allOrderListDto = orderService.orderList();
        if(allOrderListDto.size() == 0) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity(allOrderListDto, HttpStatus.OK);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity orderDetails(@PathVariable Long orderId) {
        GetOrderDetailsDto orderDetailsDto = orderService.orderDetails(orderId);

        return new ResponseEntity(orderDetailsDto, HttpStatus.OK);
    }

}
