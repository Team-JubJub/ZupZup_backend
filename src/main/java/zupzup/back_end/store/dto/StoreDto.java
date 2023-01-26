package zupzup.back_end.store.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StoreDto {

    private Long storeId;

    private String storeName; //가게이름
    private String storeAddress; //가게 주소
    private String openTime;
    private String endTime;
    private String saleTimeStart;
    private String saleTimeEnd;
    private Double longitude;
    private Double latitude;
    private List<String> eventList;

    private List<Item> storeItems = new ArrayList<>();
}