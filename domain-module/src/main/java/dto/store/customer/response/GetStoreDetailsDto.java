package dto.store.customer.response;

import domain.store.type.EnterState;
import domain.store.type.StoreCategory;
import dto.item.customer.response.ItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetStoreDetailsDto {

    private Long storeId;

    private String storeName;
    private String storeImageUrl;
    private String storeAddress;
    private StoreCategory category;
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

    private Boolean isStarred;  // 찜했는지 여부
    private Boolean isAlerted;  // 알림설정했는지 여부

    private Integer starredUserCount;   // 찜한 사용자의 수

    private List<ItemResponseDto> itemDtoList;

}
