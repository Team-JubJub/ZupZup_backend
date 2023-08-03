package dto.item.seller.request;

import lombok.Getter;
import lombok.Setter;


public class ItemRequestDto {

    @Getter @Setter
    public class patchDto {

        private Long itemId;
        private int itemCount;
    }
}
