package exception.store;

import org.springframework.http.HttpStatus;

public class ForbiddenStoreException extends StoreRuntimeException {

    public ForbiddenStoreException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

}
