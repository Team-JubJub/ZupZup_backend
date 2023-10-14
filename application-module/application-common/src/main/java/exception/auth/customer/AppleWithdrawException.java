package exception.auth.customer;

import exception.auth.AuthRuntimeException;
import org.springframework.http.HttpStatus;

public class AppleWithdrawException extends AuthRuntimeException {

    private static final String MESSAGE = "Withdraw with apple's response is 400";

    public AppleWithdrawException() {
        super(MESSAGE, HttpStatus.BAD_REQUEST);
    }

}
