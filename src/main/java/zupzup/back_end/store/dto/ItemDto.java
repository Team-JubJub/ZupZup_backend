package zupzup.back_end.store.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;

@Getter
@Setter
public class ItemDto {

    private Long id;
    private String itemName;
    private int itemPrice;
    private int discountedPrice;
    private int count;
    private Store store;
    private ItemImgDto itemImgDto;

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem() {
        return modelMapper.map(this, Item.class);
    }

    public ItemDto of(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }
}