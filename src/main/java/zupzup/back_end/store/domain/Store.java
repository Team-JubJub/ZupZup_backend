package zupzup.back_end.store.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import zupzup.back_end.converter.StringListConverter;
import zupzup.back_end.store.dto.StoreDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder @Table(name = "store")
@AllArgsConstructor
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @Setter
    @Column(nullable = false) private String storeName; //가게이름
    @Setter @Column(nullable = false, length = 1000) private String storeAddress; //가게 주소
    @Setter @Column(nullable = false) private String openTime;
    @Setter @Column(nullable = false) private String endTime;
    @Setter @Column(nullable = false) private Integer saleTimeStart;
    @Setter @Column(nullable = false) private Integer saleTimeEnd;
    @Setter @Column(nullable = false) private Double longitude;
    @Setter @Column(nullable = false) private Double latitude;
    @Convert(converter = StringListConverter.class)
    @Setter @Column private List<String> eventList;

    @OneToMany(
            mappedBy = "store",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<Item> storeItems = new ArrayList<>();

    protected Store() {}

    // 가게 데이터를 업데이트 하는 로직
    public void updateStore(StoreDto storeDto) {
        this.storeName = storeDto.getStoreName();
        this.storeAddress = storeDto.getStoreAddress();
        this.openTime = storeDto.getOpenTime();
        this.endTime = storeDto.getEndTime();
        this.saleTimeStart = storeDto.getSaleTimeStart();
        this.saleTimeEnd = storeDto.getSaleTimeEnd();
        this.longitude = storeDto.getLongitude();
        this.latitude = storeDto.getLatitude();
        this.eventList = storeDto.getEventList();
        this.storeItems = storeDto.getStoreItems();
    }
}
