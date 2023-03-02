package com.rest.api.advice;

import exception.NoSuchException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomerControllerAdvice {

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity orderConstraintViolation(ConstraintViolationException e) {
        List<String> constraintViolations = new ArrayList<>();
        e.getConstraintViolations().forEach(error -> {
            String message = error.getMessage();
            constraintViolations.add(message);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constraintViolations);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)   // 가게, 주문이 존재하지 않는 경우
    @ExceptionHandler(value = NoSuchException.class)
    public String reservationNoSuch(NoSuchException e) {
        return e.getMessage();
    }   // 후에 수정(이름 등) 필요할 듯

}
