package zupzup.back_end.store.dto.response;

import lombok.Getter;
import lombok.Setter;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;
import zupzup.back_end.store.dto.ItemDto;

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
