package domain.store;

import com.fasterxml.jackson.annotation.JsonIgnore;


import converter.StringListConverter;
import domain.item.Item;
import domain.order.Order;
import dto.store.StoreDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "StoreBuilder")
@Getter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId")
    @Getter
    private Long storeId;
    private Long fireBaseStoreId;

    private String loginId; // 분리할 예정. 의논 후 여부 결정.
    private String loginPwd;    // 상동

    @Column(nullable = false)
    private String storeName;   //가게이름
    @Column(nullable = false)
    private String category;    // 가게 카테고리
    @Column(nullable = false, length = 1000)
    private String storeAddress;    //가게 주소
    @Column(nullable = false)
    private String openTime;    // 시작 시간
    @Column(nullable = false)
    private String endTime; // 마감 시간
    @Column
    private String saleMatters; // 공지사항?
    @Column(nullable = false)
    private String saleTimeStart;   // 할인 시작 시간
    @Column(nullable = false)
    private String saleTimeEnd; // 할인 마감 시간
    @Column // nullable true
    private String salePercent; // 할인률? 이건 삭제될 듯.
    @Column(nullable = false)
    private Double longitude;   // 경도
    @Column(nullable = false)
    private Double latitude;    // 위도
    @Convert(converter = StringListConverter.class)
    @Column private List<String> eventList; // 이벤트 리스트? 스트링? 뭐가 될지 의논해볼 것.

    @OneToMany(
            mappedBy = "store", // item 객체의 store와 연결
            //cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    private List<Item> storeItems = new ArrayList<>();

    public static StoreBuilder builder(String storeName) {   // 필수 파라미터 고려해볼 것
        if(storeName == null) {
            throw new IllegalArgumentException("필수 파라미터(store name) 누락");
        }
        return StoreBuilder().storeName(storeName);
    }

    // 가게 데이터를 업데이트 하는 로직
    public void updateStore(StoreDto storeDto) {
        this.storeName = storeDto.getStoreName();
        this.storeAddress = storeDto.getStoreAddress();
        this.openTime = storeDto.getOpenTime();
        this.endTime = storeDto.getEndTime();
        this.saleMatters = storeDto.getSaleMatters();
        this.saleTimeStart = storeDto.getSaleTimeStart();
        this.saleTimeEnd = storeDto.getSaleTimeEnd();
        this.longitude = storeDto.getLongitude();
        this.latitude = storeDto.getLatitude();
        this.eventList = storeDto.getEventList();
        this.storeItems = storeDto.getStoreItems();
    }

}
