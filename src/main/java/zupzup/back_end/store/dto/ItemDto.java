package zupzup.back_end.store.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;

@Getter
@Setter
public class ItemDto {

    private String itemName;
    private String imageURL;
    private int itemPrice;
    private int salePrice;
    private int itemCount;
    private Store store;
}