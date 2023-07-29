package dto.item.seller.request;

import lombok.Getter;
import lombok.Setter;


public class ItemRequestDto {
    @Getter @Setter
    public class postDto {
        private String itemName;
        private int itemPrice;
        private int salePrice;
        private int itemCount;
    }

    @Getter @Setter
    public class patchDto {

        private Long itemId;
        private int itemCount;
    }
}
