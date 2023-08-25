package com.rest.api.advice;

import com.zupzup.untact.dto.MessageDto;
import exception.NoSuchException;
import exception.auth.customer.AlreadySignUppedException;
import exception.auth.customer.NoUserPresentsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomerControllerAdvice {

    @ExceptionHandler(value = MissingRequestHeaderException.class)
    public ResponseEntity missingRequestHeader(MissingRequestHeaderException e) {   // 요청 파라미터 중 헤더에 문제가 있는 경우
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity missingServletRequestParameter(MissingServletRequestParameterException e) {   // 요청 파라미터 중 query에 문제가 있는 경우
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity httpMessageNotReadable(HttpMessageNotReadableException e) {   // 요청 파라미터 중 바디에 문제가 있는 경우(파라미터 누락 등)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage().substring(0, 32));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)    // Request body에 대한 validation
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(value = AlreadySignUppedException.class)
    public ResponseEntity alreadySignUpped(AlreadySignUppedException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(value = NoUserPresentsException.class)
    public ResponseEntity noUserPresents(NoUserPresentsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(value = NoSuchException.class)    // 가게, 주문이 존재하지 않는 경우
    public ResponseEntity noSuchStoreOrOrder(NoSuchException e) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto(e.getMessage()));
    }   // 후에 수정(이름 등) 필요할 듯

}
