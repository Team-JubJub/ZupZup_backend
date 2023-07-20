package exception.auth;

import org.springframework.http.HttpStatus;

public class RefreshRequiredException extends AuthRuntimeException {
    private static final String MESSAGE = "redirect: /mobile/sign-in/refresh (Access token expired. Renew it with refresh token.)";

    public RefreshRequiredException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
