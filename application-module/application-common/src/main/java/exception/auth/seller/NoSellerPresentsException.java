package exception.auth.seller;

import exception.auth.AuthRuntimeException;
import org.springframework.http.HttpStatus;

public class NoSellerPresentsException extends AuthRuntimeException {

    private static final String MESSAGE = "Seller with ID doesn't present";

    public NoSellerPresentsException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
