package com.rest.api.exception.order;

import org.springframework.http.HttpStatus;

public class SellerNoSuchException extends SellerOrderRuntimeException {

    public SellerNoSuchException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
