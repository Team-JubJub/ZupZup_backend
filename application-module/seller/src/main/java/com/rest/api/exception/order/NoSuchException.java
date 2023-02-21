package com.rest.api.exception.order;

import org.springframework.http.HttpStatus;

public class NoSuchException extends OrderRuntimeException {

    public NoSuchException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
