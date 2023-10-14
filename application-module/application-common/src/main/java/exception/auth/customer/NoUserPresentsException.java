package exception.auth.customer;

import exception.auth.AuthRuntimeException;
import org.springframework.http.HttpStatus;


public class NoUserPresentsException extends AuthRuntimeException {

    private static final String MESSAGE = "User with provided unique ID doesn't present";

    public NoUserPresentsException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
