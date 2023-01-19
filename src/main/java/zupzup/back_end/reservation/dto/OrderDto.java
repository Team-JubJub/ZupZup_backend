package zupzup.back_end.reservation.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.domain.type.OrderStatus;

import java.util.List;

@Getter
@Setter
public class OrderDto { // DTO 내에  request, response 등을 inner class로 만들어주자.
    private Long id;

    private String username; // 닉네임
    private String phoneNum; // 전화번호
    private String visitTime; // 방문예정 시간
    private OrderStatus orderStatus; // 상태여부
    // private List<OrderItem> orderItemList; //상품 주문 리스트

    private static ModelMapper modelMapper = new ModelMapper();

    public Order createOrder() {
        return modelMapper.map(this, Order.class);
    }

    public OrderDto of(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

}
