package com.rest.api.reservation.domain.type;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Embeddable
public class OrderSpecific {
    //[상품 이름, 가격, 갯수, (이미지)]
    private Long itemId; // 일단 단순 Long으로 받아오고, 외래키로 갈지 말지는 나중에
    private String itemName;    // 상품 이름
    private int itemPrice;  // 가격
    @Min(value = 0, message = "상품의 개수는 0개 이하일 수 없습니다.")
    private int itemCount;  // 갯수

}
