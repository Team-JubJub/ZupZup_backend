package zupzup.back_end.reservation.Exception;

import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends OrderRuntimeException {
    private static final String MESSAGE = "해당 주문을 가게에서 찾을 수 없습니다.";

    public OrderNotFoundException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
