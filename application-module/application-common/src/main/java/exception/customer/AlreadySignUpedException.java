package exception.customer;


import domain.auth.User.Provider;
import org.springframework.http.HttpStatus;

public class AlreadySignUpedException extends AuthRuntimeException {

    private Provider provider;
    private static final String MESSAGE = "User already sign uped.";

    public AlreadySignUpedException(Provider provider) {
        super(MESSAGE + "(Platform with: " + provider.getProvider() + ")", HttpStatus.CONFLICT);
        this.provider = provider;
    }

}
