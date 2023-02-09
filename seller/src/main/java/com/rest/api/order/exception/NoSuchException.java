package com.rest.api.order.exception;

import org.springframework.http.HttpStatus;

public class NoSuchException extends OrderRuntimeException {

    public NoSuchException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
