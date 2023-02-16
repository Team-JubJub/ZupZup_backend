package dto.store.seller.response;

import dto.item.seller.response.ItemResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class StoreResponseDto {

    private Long storeId;

    private String storeName;
    private String openTime;
    private String endTime;

    private List<String> eventList;

    private String saleTimeStart;
    private String saleTimeEnd;

    private List<ItemResponseDto> storeItems = new ArrayList<>();
}
