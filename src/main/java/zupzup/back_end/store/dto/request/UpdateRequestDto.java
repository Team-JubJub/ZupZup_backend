package zupzup.back_end.store.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateRequestDto {

    private String itemName;
    private String imageURL;
    private int itemPrice;
    private int salePrice;
    private int itemCount;
    private Long storeId;
}
