package exception.store;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StoreRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public StoreRuntimeException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
