package exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public OrderRuntimeException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}