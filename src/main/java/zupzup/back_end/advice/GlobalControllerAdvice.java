package zupzup.back_end.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zupzup.back_end.reservation.exception.NoSuchException;
import zupzup.back_end.reservation.exception.OrderNotInStoreException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NoSuchException.class)
    public String reservationNoSuch(NoSuchException e) {
        return e.getMessage();
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = OrderNotInStoreException.class)
    public String reservationOrderNotInStore(OrderNotInStoreException e) {
        return e.getMessage();
    }
}
