package dto.store.customer.response;

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
    private String category;
    private String sellerName;
    private String sellerContact;

    private Double longitude;
    private Double latitude;
    private String openTime;
    private String closeTime;
    private String saleTimeStart;
    private String saleTimeEnd;
    private String saleMatters;

    private Boolean isOpen;
    private String closedDay;

    private List<ItemResponseDto> itemDtoList;

}
