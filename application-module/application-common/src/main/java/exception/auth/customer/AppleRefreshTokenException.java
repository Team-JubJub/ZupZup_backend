package exception.auth.customer;

import exception.auth.AuthRuntimeException;
import org.springframework.http.HttpStatus;

public class AppleRefreshTokenException extends AuthRuntimeException {

    private static final String MESSAGE = "Withdraw with apple's response is 400";

    public AppleRefreshTokenException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }

}
