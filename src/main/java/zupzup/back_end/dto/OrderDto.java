package zupzup.back_end.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import zupzup.back_end.domain.Order;
import zupzup.back_end.domain.type.OrderStatus;
import zupzup.back_end.dto.request.OrderRequestDto;

import java.util.List;

@Getter @Setter
public class OrderDto {

    private Long id;

    private String username; // 닉네임
    private String phoneNum; // 전화번호
    private String visitTime; // 방문예정 시간
    private OrderStatus orderStatus; // 상태여부
    private List<OrderRequestDto> orderList; //상품 주문 리스트

    private static ModelMapper modelMapper = new ModelMapper();

    public Order createOrder() {
        return modelMapper.map(this, Order.class);
    }

    public OrderDto of(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
