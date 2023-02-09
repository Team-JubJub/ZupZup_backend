package com.rest.api.reservation.exception;

import org.springframework.http.HttpStatus;

public class NoSuchException extends OrderRuntimeException {

    public NoSuchException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
