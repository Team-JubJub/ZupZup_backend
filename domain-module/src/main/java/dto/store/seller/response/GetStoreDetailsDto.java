package dto.store.seller.response;

import domain.store.type.StoreCategory;
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
    private Long sellerId;
    private String storeName; //가게이름
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
    private String saleMatters; //영업 관련 사항 / ex. 공휴일 휴무
    private String promotion;

    private Boolean isOpen;
    private String closedDay;

    private List<Long> starredUsers;

    private String crNumber;    // 사업자 등록번호

}