package com.rest.api.store.dto;

import com.rest.api.store.domain.Item;
import lombok.Getter;
import lombok.Setter;
import com.rest.api.store.domain.Store;

@Getter
@Setter
public class ItemDto {

    private String itemName;
    private String imageURL;
    private int itemPrice;
    private int salePrice;
    private int itemCount;
    private Store store;

    public ItemDto toItemDto(Item item) {

        this.itemName = item.getItemName();
        this.imageURL = item.getImageURL();
        this.itemPrice = item.getItemPrice();
        this.salePrice = item.getSalePrice();
        this.itemCount = item.getItemCount();
        this.store = item.getStore();

        return this;
    }
}