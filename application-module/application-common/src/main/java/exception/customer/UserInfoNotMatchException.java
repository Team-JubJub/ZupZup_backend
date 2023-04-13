package exception.customer;

import org.springframework.http.HttpStatus;

public class UserInfoNotMatchException extends AuthRuntimeException {

    private static final String MESSAGE = "User info requested does not match.";

    public UserInfoNotMatchException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
