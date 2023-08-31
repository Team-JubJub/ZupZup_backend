package exception.item;

import org.springframework.http.HttpStatus;

public class ItemRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public ItemRuntimeException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
