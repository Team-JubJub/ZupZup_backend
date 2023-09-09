package dto.order.seller.response;

import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetOrderDetailsDto { // 단건 GET에 mapping할 DTO

    private Long orderId;    // order ID
    private Long storeId;
    private Long userId;

    private OrderStatus orderStatus;
    private String userName; // 닉네임
    private String phoneNumber;
    private String orderTitle; // ex) 크로플 3개 외 3
    private String orderTime; // 주문 시간
    private String visitTime; // 방문예정 시간
    private String storeName;
    private String storeAddress;
    private String category;

    private List<OrderSpecific> orderList;

}
