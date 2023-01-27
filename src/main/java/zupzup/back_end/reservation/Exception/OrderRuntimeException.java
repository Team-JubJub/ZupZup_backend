package zupzup.back_end.reservation.Exception;

import org.springframework.http.HttpStatus;

public class OrderRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public OrderRuntimeException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}