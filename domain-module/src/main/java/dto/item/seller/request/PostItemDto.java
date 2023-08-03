package dto.item.seller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostItemDto {

    private String itemName;
    private int itemPrice;
    private int salePrice;
    private int itemCount;

}