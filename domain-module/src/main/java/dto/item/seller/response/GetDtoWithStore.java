package dto.item.seller.response;

import domain.store.Store;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetDtoWithStore {

    private String itemName;
    private String imageURL;
    private int itemPrice;
    private int salePrice;
    private int itemCount;
    private Store store;

}
