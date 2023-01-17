package zupzup.back_end.reservation.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderRequestDto {  //set upstream test
    //[상품 이름, 가격, 갯수]

    private String itemName;

    private int itemPrice;

    private int itemCount;
}
