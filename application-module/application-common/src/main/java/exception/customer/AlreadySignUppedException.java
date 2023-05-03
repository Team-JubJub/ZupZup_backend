package exception.customer;


import domain.auth.User.Provider;
import org.springframework.http.HttpStatus;

public class AlreadySignUppedException extends AuthRuntimeException {

    private Provider provider;
    private static final String MESSAGE = "User already sign-upped.";

    public AlreadySignUppedException(Provider provider) {
        super(MESSAGE + "(Platform with: " + provider.getProvider() + ")", HttpStatus.CONFLICT);
        this.provider = provider;
    }

}
