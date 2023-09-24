package domain.store;


import domain.store.type.EnterState;
import domain.store.type.StoreCategory;
import dto.store.StoreDto;
import dto.store.seller.request.ModifyStoreDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.List;
import java.util.Set;

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
    @Enumerated(EnumType.STRING) @Column(nullable = true)
    private StoreCategory category; // 카테고리
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
    @Column(nullable = true)
    private String promotion; // 공지와 별개의 프로모션

    @Column
    private Boolean isOpen; // 가게 운영 여부
    @Column(nullable = true)
    private String closedDay; // 휴무일 (0-휴무, 1-영업)

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "starredUsers", joinColumns = @JoinColumn(name="storeId", referencedColumnName="storeId"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> starredUsers;    // 찜한 유저 아이디들 -> 사용자 앱에서 찜 설정 시 조작됨
    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "alertUsers", joinColumns = @JoinColumn(name="storeId", referencedColumnName="storeId"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> alertUsers;    // 알림 설정한 유저 아이디들 -> 사용자 앱에서 알림 설정 시 조작됨

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "deviceTokens", joinColumns = @JoinColumn(name="storeId", referencedColumnName="storeId"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<String> deviceTokens;    // 알림 설정한 유저 아이디들

    @Column(nullable = false)
    private String crNumber;    // 사업자 등록번호

    @Enumerated(EnumType.STRING) @Column(nullable = true)
    private EnterState enterState;  // 등록 상태(NEW, WAIT, CONFIRM)

    public static StoreBuilder builder(String storeName) {   // 필수 파라미터 고려해볼 것
        if(storeName == null) {
            throw new IllegalArgumentException("필수 파라미터(store name) 누락");
        }
        return StoreBuilder().storeName(storeName);
    }

    // 가게 deviceTokens를 업데이트하는 로직
    public void modifyDeviceTokens(Set<String> deviceTokens) {
        this.deviceTokens = deviceTokens;
    }

    // 가게 데이터를 업데이트하는 로직
    public void modifyStore(ModifyStoreDto modifyStoreDto) {
        this.storeImageUrl = modifyStoreDto.getStoreImageUrl();
        this.openTime = modifyStoreDto.getOpenTime();
        this.closeTime = modifyStoreDto.getCloseTime();
        this.saleTimeStart = modifyStoreDto.getSaleTimeStart();
        this.saleTimeEnd = modifyStoreDto.getSaleTimeEnd();
        this.closedDay = modifyStoreDto.getClosedDay();
    }

    public void updateStarredUserList(StoreDto storeDto) {
        this.starredUsers = storeDto.getStarredUsers();
    }

    public void updateAlertUserList(StoreDto storeDto) {
        this.alertUsers = storeDto.getStarredUsers();
    }

}
