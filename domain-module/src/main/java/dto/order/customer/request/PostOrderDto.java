package dto.order.customer.request;

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
public class PostOrderDto {

    private String userName; // 닉네임
    private String phoneNumber; // 전화번호 -> 필요 없으면 삭제할 것
    private String visitTime;
    private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수

}