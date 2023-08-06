package dto.store;

import domain.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StoreDto {

    private Long storeId;

    private String storeName; //가게이름
    private String storeAddress; //가게 주소
    private String openTime;
    private String endTime;
    private String saleMatters; //영업 관련 사항 / ex. 공휴일 휴무
    private String saleTimeStart;
    private String saleTimeEnd;
    private Double longitude;
    private Double latitude;
    private List<String> eventList;

    private List<Item> storeItems = new ArrayList<>();
}