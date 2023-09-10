package domain.order;

import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import dto.order.OrderDto;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자는 필요, access level 설정을 통해 new Order();와 같이 아무런 값을 갖지 않는 의미 없는 객체 생성 방지
@AllArgsConstructor(access = AccessLevel.PRIVATE)   // Builder 적용을 위한 생성자, access level 설정을 통해 Builder를 제외한 외부에서의 field 조작 방지
@Builder(builderMethodName = "OrderBuilder")
@Getter
@Table(name = "orders")  // table name이 order -> SQL 예약어와 동일, table 명 수정.
public class Order {

    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;   // auto increment id
    @Column(nullable = false) private Long storeId;
    @Column(nullable = false) private Long userId;

    @Enumerated(EnumType.STRING) @NotNull
    private OrderStatus orderStatus;    // 주문 상태
    @Column(nullable = false) private String userName; // 예약자명
    @Column(nullable = false) private String phoneNumber; // 예약자 전화번호
    @Column(nullable = false) private String orderTitle; // ex) 크로플 3개 외 3
    @Column(nullable = false) private String orderTime; // 주문 시간(LocalDateTime, 현재는 KST 기준)
    @Column(nullable = false) private String visitTime; // 방문예정 시간(LocalDateTime, 현재는 KST 기준)
    @Column(nullable = false) private String storeName; // 가게 이름
    @Column(nullable = false) private String storeAddress;  // 가게 주소
    @Column(nullable = false) private String category;  // 가게 카테고리

    @NotNull @ElementCollection
    @CollectionTable(name = "orderSpecific", joinColumns = @JoinColumn(name="orderId", referencedColumnName="orderId"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL) @Valid
    private List<OrderSpecific> orderList;  // 주문 품목(이름, 가격, 개수, (img))

    @Column(nullable = false) private Integer totalPrice;   // 주문의 총 금액(할인 가격의 합)
    @Column(nullable = false) private Integer savedMoney;   // 아낀 금액(판매 가격의 합 - 할인 가격의 합)

    public static OrderBuilder builder(Long storeId) {   // 필수 파라미터 고려해볼 것
        if(storeId == null) {
            throw new IllegalArgumentException("필수 파라미터(store) 누락");
        }
        return OrderBuilder().storeId(storeId);
    }

    public void updateOrder(OrderDto orderDto) {
        this.orderStatus = orderDto.getOrderStatus();
        this.orderList = orderDto.getOrderList();
    }

}
