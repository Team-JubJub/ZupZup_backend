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
    private Integer saleTimeStart;
    private Integer saleTimeEnd;
    private Double longitude;
    private Double latitude;
    private List<String> eventList;

    private List<Item> storeItems = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();

    public Store creatStore() {
        return modelMapper.map(this, Store.class);
    }

    public static StoreDto of(Store store) {
        return modelMapper.map(store, StoreDto.class);
    }
}