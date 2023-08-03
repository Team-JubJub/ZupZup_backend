package dto.item.seller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostItemDto {

    private String itemName;
    private int itemPrice;
    private int salePrice;
    private int itemCount;

}