package exception.store.seller;

import exception.store.StoreRuntimeException;
import org.springframework.http.HttpStatus;

public class NoSuchStoreException extends StoreRuntimeException {

    public NoSuchStoreException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
