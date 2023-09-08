package dto.store;

import domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StoreDto {

    private Long storeId;

    private String storeName; //가게이름
    private String storeImageUrl;
    private String storeAddress; //가게 주소
    private String category;
    private String sellerName;
    private String sellerContact;
    private String storeContact;

    private Double longitude;
    private Double latitude;
    private String openTime;
    private String closeTime;
    private String saleTimeStart;
    private String saleTimeEnd;
    private String saleMatters;
    private String promotion;

    private Boolean isOpen;
    private String closedDay;

    private List<Integer> starredUsers;

    private String crNumber;

    private List<Item> storeItems;

}