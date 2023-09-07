package domain.store;


import dto.store.seller.request.ModifyStoreDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "StoreBuilder")
@Getter @Setter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId")
    @Getter
    private Long storeId;

    @JoinColumn(name = "seller")
    private Long sellerId;

    @Column(nullable = false)
    private String storeName; // 가게이름
    @Column(nullable = true)
    private String storeImageUrl; // 가게 대표 이미지 url - 이미지 없을 시 기본이미지
    @Column(nullable = false, length = 1000)
    private String storeAddress; // 가게 주소
    @Column(nullable = true)
    private String category; // 카테고리
    @Column(nullable = false)
    private String sellerName; // 대표자 이름
    @Column(nullable = false)
    private String sellerContact; // 대표자 연락처
    @Column(nullable = true)
    private String storeContact; // 가게 연락처

    @Column(nullable = true)
    private Double longitude;   // 경도
    @Column(nullable = true)
    private Double latitude;    // 위도
    @Column(nullable = true)
    private String openTime; // 운영 시작 시간
    @Column(nullable = true)
    private String closeTime; // 운영 마감 시간
    @Column(nullable = true)
    private String saleTimeStart;   // 할인 시작 시간
    @Column(nullable = true)
    private String saleTimeEnd; // 할인 마감 시간
    @Column(nullable = true)
    private String saleMatters; // 공지사항

    @Column
    private Boolean isOpen; // 가게 운영 여부
    @Column(nullable = true)
    private String closedDay; // 휴무일 (0-휴무, 1-영업)

    public static StoreBuilder builder(String storeName) {   // 필수 파라미터 고려해볼 것
        if(storeName == null) {
            throw new IllegalArgumentException("필수 파라미터(store name) 누락");
        }
        return StoreBuilder().storeName(storeName);
    }

    // 가게 데이터를 업데이트 하는 로직
    public void modifyStore(ModifyStoreDto modifyStoreDto) {
        this.storeImageUrl = modifyStoreDto.getStoreImageUrl();
        this.openTime = modifyStoreDto.getOpenTime();
        this.closeTime = modifyStoreDto.getCloseTime();
        this.saleTimeStart = modifyStoreDto.getSaleTimeStart();
        this.saleTimeEnd = modifyStoreDto.getSaleTimeEnd();
        this.closedDay = modifyStoreDto.getClosedDay();
    }

}
