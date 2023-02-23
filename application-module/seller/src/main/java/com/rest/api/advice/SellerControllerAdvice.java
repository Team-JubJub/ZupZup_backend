package com.rest.api.advice;

import com.rest.api.exception.order.NoSuchException;
import com.rest.api.exception.order.OrderNotInStoreException;
import com.rest.api.exception.order.RequestedCountExceedStockException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SellerControllerAdvice {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)   // 가게, 주문이 존재하지 않는 경우
    @ExceptionHandler(value = NoSuchException.class)
    public String reservationNoSuch(NoSuchException e) {
        return e.getMessage();
    }   // 후에 수정(이름 등) 필요할 듯

    @ResponseStatus(value = HttpStatus.NOT_FOUND)   // 주문이 해당 가게의 주문이 아닌 경우 -> BAD_REQUEST가 맞는지 고민해볼 것
    @ExceptionHandler(value = OrderNotInStoreException.class)
    public String reservationOrderNotInStore(OrderNotInStoreException e) {
        return e.getMessage();
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST) // 주문확정 시 상품 개수가 재고를 초과했을 경우(하나라도 초과하면)
    @ExceptionHandler(value = RequestedCountExceedStockException.class)
    public String reservationExceedStock(RequestedCountExceedStockException e) { return e.getMessage();}

}
