package com.rest.api.exception.order;

import org.springframework.http.HttpStatus;

public class SellerOrderNotInStoreException extends SellerOrderRuntimeException {
    private static final String MESSAGE = "해당 주문은 이 가게의 주문이 아닙니다.";

    public SellerOrderNotInStoreException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
