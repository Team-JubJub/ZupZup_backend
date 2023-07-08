package dto.order;

import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Long orderId;    // Order ID
    private Long storeId;
    private Long userId;
    
    private OrderStatus orderStatus; // 주문 상태
    private String userName; // 닉네임
    private String phoneNumber; // 전화번호 -> 필요 없으면 삭제할 것
    private String orderTitle;  // ex) 크로플 3개 외 3개
    private String orderTime;   // 주문 시간
    private String visitTime; // 방문예정 시간
    private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
}
