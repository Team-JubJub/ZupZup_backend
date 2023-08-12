package dto.order.customer.response;

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
public class GetOrderDto {

    private Long orderId;
    private Long storeId;
    private Long userId;
    private OrderStatus orderStatus;
    private String userName; // 닉네임
    private String phoneNumber; // 유저의 전화번호
    private String orderTitle;
    private String orderTime;
    private String visitTime;
    private String storeName;
    private String storeAddress;
    private String category;
    private List<OrderSpecific> orderList;

}
