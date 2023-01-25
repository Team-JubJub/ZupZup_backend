package zupzup.back_end.store.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemRequestDto {

    private String itemName;
    private int itemPrice;
    private int salePrice;
    private int itemCount;
    private Long storeId;
}