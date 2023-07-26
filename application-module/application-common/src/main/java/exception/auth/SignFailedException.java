package exception.auth;

import org.springframework.http.HttpStatus;

public class SignFailedException extends  AuthRuntimeException {

    public SignFailedException() {
        super("Signature does not match locally computed signature", HttpStatus.UNAUTHORIZED);
    }

}
