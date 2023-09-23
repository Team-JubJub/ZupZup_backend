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
public class GetOrderDto {

    private Long orderId;
    private Long storeId;
    private Long userId;

    private OrderStatus orderStatus;
    private String orderTitle;
    private String orderTime;
    private String storeName;
    private StoreCategory category;

    private Integer totalPrice;

}
