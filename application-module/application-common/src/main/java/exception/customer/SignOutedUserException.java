package exception.customer;

import org.springframework.http.HttpStatus;

public class SignOutedUserException extends AuthRuntimeException {

    private static final String MESSAGE = "Sign outed user. Please sign in again.";

    public SignOutedUserException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
