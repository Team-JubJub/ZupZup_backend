package com.rest.api.exception.order;

import org.springframework.http.HttpStatus;

public class SellerOrderRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public SellerOrderRuntimeException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}