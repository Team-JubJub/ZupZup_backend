package dto.store.customer.response;

import dto.item.seller.response.ItemResponseDto;
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
    private String category;
    private String storeAddress;

    private String openTime;
    private String endTime;
    private String saleMatters;

    private String saleTimeStart;
    private String saleTimeEnd;

    private String salePercent;

    private Double longitude;
    private Double latitude;

    private List<String> eventList;

    private List<ItemResponseDto> itemDtoList;

}
