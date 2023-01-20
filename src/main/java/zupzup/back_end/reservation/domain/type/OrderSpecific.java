package zupzup.back_end.reservation.domain.type;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class OrderSpecific {
    //[상품 이름, 가격, 갯수, (이미지)]
    private String itemName;    // 상품 이름
    private int itemPrice;  // 가격
    private int itemCount;  // 갯수
    //private String imgURL;  // 이미지
}
