package exception.customer;


import domain.auth.User.Provider;
import org.springframework.http.HttpStatus;

public class AlreadySignedUpException extends AuthRuntimeException {

    private Provider provider;
    private static final String MESSAGE = "이미 가입된 유저입니다.";

    public AlreadySignedUpException(Provider provider) {
        super(MESSAGE + "(" + provider.getProvider() + "로 가입)", HttpStatus.CONFLICT);
        this.provider = provider;
    }

}
