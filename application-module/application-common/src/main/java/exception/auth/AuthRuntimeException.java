package exception.auth;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthRuntimeException extends RuntimeException {

    private final HttpStatus httpStatus;

    public AuthRuntimeException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
