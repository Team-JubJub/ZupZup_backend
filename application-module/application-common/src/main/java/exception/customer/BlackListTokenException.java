package exception.customer;

import org.springframework.http.HttpStatus;

public class BlackListTokenException extends AuthRuntimeException {

    private static final String MESSAGE = "Sign-outed or deleted user. Please sign-in or sign-up again.";

    public BlackListTokenException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
