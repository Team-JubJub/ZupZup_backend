package dto.item.seller.response;

import domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemResponseDto {

    private Long id;
    private String itemName;
    private String imageURL;
    private int itemPrice;
    private int salePrice;
    private int itemCount;
    //private Long storeId;

    public ItemResponseDto toItemResponseDto(Item item) {

        this.id = item.getItemId();
        this.itemName = item.getItemName();
        this.imageURL = item.getImageURL();
        this.itemPrice = item.getItemPrice();
        this.salePrice = item.getSalePrice();
        this.itemCount = item.getItemCount();
        //this.storeId = item.getStore().getStoreId();

        return this;
    }
}
