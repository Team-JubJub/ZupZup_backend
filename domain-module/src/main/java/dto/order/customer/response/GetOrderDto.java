package dto.order.customer.response;

import domain.order.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetOrderDto {

    private Long orderId;
    private String storeName;   // entity에는 store 객체 저장, modelMapper가 알아서 name 빼내줌.
    private OrderStatus orderStatus;
    private String userName; // 닉네임
    private String orderTitle;
    private String orderTime;
    private String visitTime;

}