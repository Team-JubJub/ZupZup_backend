package zupzup.back_end.reservation.exception;

import org.springframework.http.HttpStatus;

public class OrderNotInStoreException extends OrderRuntimeException {
    private static final String MESSAGE = "해당 주문은 이 가게의 주문이 아닙니다.";

    public OrderNotInStoreException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
