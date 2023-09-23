package dto.order.customer.response;

import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.type.StoreCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetOrderDetailsDto {

    private Long orderId;
    private Long storeId;
    private Long userId;

    private OrderStatus orderStatus;
    private String userName;
    private String phoneNumber;
    private String orderTitle;
    private String orderTime;
    private String visitTime;
    private String storeName;
    private String storeAddress;
    private StoreCategory category;

    private List<OrderSpecific> orderList;
    private Integer totalPrice;
    private Integer savedMoney;
    
}
