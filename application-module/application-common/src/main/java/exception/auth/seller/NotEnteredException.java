package exception.auth.seller;

import exception.auth.AuthRuntimeException;
import org.springframework.http.HttpStatus;

public class NotEnteredException extends AuthRuntimeException {

    private static final String MESSAGE = "Seller not entered yet.";


    public NotEnteredException() { super(MESSAGE, HttpStatus.UNAUTHORIZED); }
}
