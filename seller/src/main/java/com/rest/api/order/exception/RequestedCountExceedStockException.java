package com.rest.api.order.exception;

import org.springframework.http.HttpStatus;

public class RequestedCountExceedStockException extends OrderRuntimeException {
    private Long itemId;
    private String itemName;
    private static final String MESSAGE = "주문 중 상품의 재고가 주문 확정한 개수보다 부족합니다. 상품 명(ID): ";

    public RequestedCountExceedStockException(Long itemId, String itemName) {
        super(MESSAGE + itemName + "(" + itemId + ")", HttpStatus.BAD_REQUEST);
        this.itemId = itemId;
        this.itemName = itemName;
    }
}
