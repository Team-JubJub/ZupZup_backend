package dto.item;

import domain.item.Item;
import domain.store.Store;
import lombok.Getter;
import lombok.Setter;


public class ItemDto {

    @Getter @Setter
    public class getDtoWithStore {
        private String itemName;
        private String imageURL;
        private int itemPrice;
        private int salePrice;
        private int itemCount;
        private Store store;
    }

    @Getter @Setter
    public class getDto {
        private Long itemId;
        private String itemName;
        private String imageURL;
        private int itemPrice;
        private int salePrice;
        private int itemCount;
    }
}