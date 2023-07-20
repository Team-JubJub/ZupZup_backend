package exception.auth;

import org.springframework.http.HttpStatus;

public class RequiredHeaderNotExistException extends AuthRuntimeException {

    private String requiredParameter;

    private static final String MESSAGE = "Required header parameter(";

    public RequiredHeaderNotExistException(String requiredParameter) {
        super(MESSAGE + requiredParameter + ") does not exits", HttpStatus.FORBIDDEN);
        this.requiredParameter = requiredParameter;
    }

}
