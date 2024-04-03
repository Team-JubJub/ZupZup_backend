package com.rest.api.advice;

import com.zupzup.untact.model.dto.MessageDto;
import exception.NoSuchException;
import exception.OrderNotInStoreException;
import exception.RequestedCountExceedStockException;
import exception.auth.seller.NoSellerPresentsException;
import exception.auth.seller.NotEnteredException;
import exception.auth.seller.WantDeletionSellerException;
import exception.item.seller.NoSuchItemException;
import exception.store.ForbiddenStoreException;
import exception.store.seller.NoSuchStoreException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class SellerControllerAdvice {

    @ExceptionHandler(value = NoSellerPresentsException.class)
    public ResponseEntity noSellerWithLoginId(NoSellerPresentsException e) { // 해당 id를 가진 사장님이 없는 경우
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(value = WantDeletionSellerException.class)
    public ResponseEntity wantDeletionSeller(WantDeletionSellerException e) { // 회원탈퇴를 진행 중인 사장님의 경우
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(value = NotEnteredException.class)
    public ResponseEntity notEntered(NotEnteredException e) {   // 아직 입점하지 않은 사장님이 앱에 로그인 시 401 리턴
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    // < ---------------- 가게 파트 ---------------- >
    @ExceptionHandler(value = ForbiddenStoreException.class)
    public ResponseEntity forbiddenStoreException(ForbiddenStoreException e) {  // 접근이 불가한 가게(사장님 앱 : NEW 상태인 가게)
        return ResponseEntity.status(e.getHttpStatus()).body(new MessageDto(e.getMessage()));
    }

    @ExceptionHandler(value = NoSuchStoreException.class)
    public ResponseEntity noSuchStore(NoSuchStoreException e) { // 존재하지 않는 가게인 경우
        return ResponseEntity.status(e.getHttpStatus()).body(new MessageDto(e.getMessage()));
    }

    @ExceptionHandler(value = NoSuchException.class)
    public ResponseEntity sellerEntireNoSuch(NoSuchException e) { // 가게, 주문이 존재하지 않는 경우
        return ResponseEntity.status(e.getHttpStatus()).body(new MessageDto(e.getMessage()));
    }   // 후에 수정(이름 등) 필요할 듯

    // < ---------------- 상품 파트  ---------------- >
    @ExceptionHandler(value = NoSuchItemException.class)
    public ResponseEntity noSuchItem(NoSuchItemException e) { // 존재하지 않는 상품의 경우
        return ResponseEntity.status(e.getHttpStatus()).body(new MessageDto(e.getMessage()));
    }

    // < ---------------- 주문 파트  ---------------- >
//    @ExceptionHandler(value = NoSuchItemException.class)  // 일단 예외처리, 나중에 에러 핸들 다시 하기
//    public ResponseEntity noSuchOrder(NoSuchException e) { // 존재하지 않는 상품의 경우
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageDto(e.getMessage()));
//    }

    @ExceptionHandler(value = OrderNotInStoreException.class)   // 주문이 해당 가게의 주문이 아닌 경우 -> BAD_REQUEST가 맞는지 고민해볼 것
    public ResponseEntity reservationOrderNotInStore(OrderNotInStoreException e) {

        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity orderConstraintViolation(ConstraintViolationException e) {
        List<String> constraintViolations = new ArrayList<>();
        e.getConstraintViolations().forEach(error -> {
            Stream<Path.Node> propertyStream = StreamSupport.stream(error.getPropertyPath().spliterator(), false);
            List<Path.Node> propertyList = propertyStream.collect(Collectors.toList());
            String wrongItem = propertyList.get(0).toString();  // ex) "orderList[index]"
            String wrongField = propertyList.get(propertyList.size()-1).getName();   // ex) "itemCount"
            String exceptionMessage = error.getMessage();    // ex) "상품이 개수는 0개 미만일 수 없습니다." -> valid에 적어놓은 message
            String invalidValue = error.getInvalidValue().toString();   // ex) -3(잘못 요청한 개수)

            constraintViolations.add(wrongItem + ", " + wrongField + ": " + exceptionMessage + "(잘못된 요청 값: " + invalidValue + ")");
            // ex) "[orderList[0], itemCount: 상품의 개수는 0개 미만일 수 없습니다.(잘못된 요청 값: -3), ...]"
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(constraintViolations);
    }

    @ExceptionHandler(value = RequestedCountExceedStockException.class) // 주문확정 시 상품 개수가 재고를 초과했을 경우(하나라도 초과하면)
    public ResponseEntity reservationExceedStock(RequestedCountExceedStockException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }

}
