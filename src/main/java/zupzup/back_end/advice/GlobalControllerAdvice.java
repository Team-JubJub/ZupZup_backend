package zupzup.back_end.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zupzup.back_end.reservation.exception.OrderNotFoundException;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = OrderNotFoundException.class)
    public String reservationOrderNotFound(OrderNotFoundException e) {
        return e.getMessage();
    }
}
