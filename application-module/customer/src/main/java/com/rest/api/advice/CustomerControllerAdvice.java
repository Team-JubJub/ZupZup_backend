package com.rest.api.advice;

import exception.NoSuchException;
import exception.customer.AlreadySignedUpException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class CustomerControllerAdvice {

    @ExceptionHandler(value = AlreadySignedUpException.class)
    public ResponseEntity alreadySignedUp(AlreadySignedUpException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity orderConstraintViolation(ConstraintViolationException e) {
        List<String> constraintViolations = new ArrayList<>();
        e.getConstraintViolations().forEach(error -> {
            Stream<Path.Node> propertyStream = StreamSupport.stream(error.getPropertyPath().spliterator(), false);
            List<Path.Node> propertyList = propertyStream.collect(Collectors.toList());
            String wrongItem = propertyList.get(0).toString();  // ex) "orderList[index]"
            String wrongField = propertyList.get(propertyList.size()-1).getName();   // ex) "itemCount"
            String exceptionMessage = error.getMessage();    // ex) "상품이 개수는 0개 미만일 수 없습니다." -> valid에 적어놓은 message
            String invalidValue = error.getInvalidValue().toString();   // ex) -3(잘못 요청한 개수)

            constraintViolations.add(wrongItem + ", " + wrongField + ": " + exceptionMessage + "(잘못된 요청 값: " + invalidValue + ")");
            // ex) "[orderList[0], itemCount: 상품의 개수는 0개 미만일 수 없습니다.(잘못된 요청 값: -3), ...]"
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constraintViolations);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)   // 가게, 주문이 존재하지 않는 경우
    @ExceptionHandler(value = NoSuchException.class)
    public String reservationNoSuch(NoSuchException e) {
        return e.getMessage();
    }   // 후에 수정(이름 등) 필요할 듯

}
