package com.rest.api.store.dto.response;

import com.rest.api.store.domain.Item;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemResponseDto {

    private String itemName;
    private String imageURL;
    private int itemPrice;
    private int salePrice;
    private int itemCount;
    private Long storeId;

    public ItemResponseDto toItemResponseDto(Item item) {

        this.itemName = item.getItemName();
        this.imageURL = item.getImageURL();
        this.itemPrice = item.getItemPrice();
        this.salePrice = item.getSalePrice();
        this.itemCount = item.getItemCount();
        this.storeId = item.getStore().getStoreId();

        return this;
    }
}
