package exception.auth.customer;


import com.zupzup.untact.domain.auth.User.Provider;
import exception.auth.AuthRuntimeException;
import org.springframework.http.HttpStatus;

public class AlreadySignUppedException extends AuthRuntimeException {

    private Provider provider;
    private static final String MESSAGE = "User already sign-upped. ";

    public AlreadySignUppedException(Provider provider) {
        super(MESSAGE + "(Platform with: " + provider.getProvider() + ")", HttpStatus.CONFLICT);
        this.provider = provider;
    }

}
