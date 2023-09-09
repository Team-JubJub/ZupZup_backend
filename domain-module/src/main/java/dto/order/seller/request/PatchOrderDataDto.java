package dto.order.seller.request;

import domain.order.type.OrderSpecific;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchOrderDataDto {

    private List<OrderSpecific> orderList; // 주문 품목 이름, 판매가격, 할인가격, 개수

}