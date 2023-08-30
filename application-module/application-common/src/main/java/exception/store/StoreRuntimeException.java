package exception.store;

import org.springframework.http.HttpStatus;

public class StoreRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public StoreRuntimeException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
