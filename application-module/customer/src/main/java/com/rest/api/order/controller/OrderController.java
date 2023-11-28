package com.rest.api.order.controller;

import com.rest.api.auth.jwt.JwtTokenProvider;
import com.rest.api.order.service.OrderService;
import com.zupzup.untact.dto.order.customer.request.PostOrderRequestDto;
import com.zupzup.untact.dto.order.customer.response.GetOrderDetailsDto;
import com.zupzup.untact.dto.order.customer.response.PostOrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    // <-------------------- POST part -------------------->
    @Operation(summary = "유저의 주문 요청", description = "유저의 주문 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "유저의 주문 성공",
                    content = @Content(schema = @Schema(implementation = PostOrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @PostMapping("/store/{storeId}")
    public ResponseEntity addOrder(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                   @PathVariable Long storeId, @RequestBody @Valid PostOrderRequestDto postOrderRequestDto) {
        PostOrderResponseDto postOrderResponseDto = orderService.addOrder(accessToken, storeId, postOrderRequestDto);

        return new ResponseEntity(postOrderResponseDto, HttpStatus.CREATED);
    }

    // <-------------------- GET part -------------------->
    @Operation(summary = "유저의 주문 정보 반환", description = "유저의 주문 정보 반환 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저의 주문 정보(리스트) 반환 성공",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = GetOrderDetailsDto.class)))
                    }
            ),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again.")))
    })
    @GetMapping("")
    public ResponseEntity orderList(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken) {
        List<GetOrderDetailsDto> allOrderListDto = orderService.orderList(accessToken);
        
        return new ResponseEntity(allOrderListDto, HttpStatus.OK);
    }

    @Operation(summary = "주문의 상세 정보 반환", description = "주문의 상세 정보 반환 요청")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문의 상세 정보 반환 성공",
                    content = @Content(schema = @Schema(implementation = GetOrderDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = "요청에 필요한 헤더(액세스 토큰)가 없음",
                    content = @Content(schema = @Schema(example = "Required header parameter(accessToken) does not exits"))),
            @ApiResponse(responseCode = "401", description = "액세스 토큰 만료 or 로그아웃 혹은 회원탈퇴한 회원의 액세스 토큰",
                    content = @Content(schema = @Schema(example = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)\n"
                            + "or Sign-outed or deleted user. Please sign-in or sign-up again."))),
            @ApiResponse(responseCode = "404", description = "해당 주문을 찾을 수 없음",
                    content = @Content(schema = @Schema(example = "{\n\t\"message\": \"해당 주문을 찾을 수 없습니다.\"\n}")))
    })
    @GetMapping("/{orderId}")
    public ResponseEntity orderDetails(@Parameter(name = "accessToken", description = "액세스 토큰", in = ParameterIn.HEADER) @RequestHeader(JwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken, @PathVariable Long orderId) {
        GetOrderDetailsDto orderDetailsDto = orderService.orderDetails(orderId);

        return new ResponseEntity(orderDetailsDto, HttpStatus.OK);
    }

}
