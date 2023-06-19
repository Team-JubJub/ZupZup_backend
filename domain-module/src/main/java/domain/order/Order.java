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
    @ManyToOne(optional = false) @JoinColumn(name = "storeId")
    private Store store;    // store (relation with store table)

    @Enumerated(EnumType.STRING) @NotNull
    private OrderStatus orderStatus;    // 주문 상태
    @NotNull private String userName; // 예약자명
    @NotNull private String phoneNumber; // 예약자 전화번호
    @NotNull private String orderTitle; // ex) 크로플 3개 외 3
    @NotNull private String orderTime; // 주문 시간(LocalDateTime, 현재는 KST 기준)
    @NotNull private String visitTime; // 방문예정 시간(LocalDateTime, 현재는 KST 기준)
    @NotNull @ElementCollection
    @CollectionTable(name = "orderSpecific", joinColumns = @JoinColumn(name="orderId", referencedColumnName="orderId"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL) @Valid
    private List<OrderSpecific> orderList;  // 주문 품목(이름, 가격, 개수, (img))

    public static OrderBuilder builder(Store store) {   // 필수 파라미터 고려해볼 것
        if(store == null) {
            throw new IllegalArgumentException("필수 파라미터(store) 누락");
        }
        return OrderBuilder().store(store);
    }

//    @Builder
//    public Order(OrderDto orderDto) {   // customer - service - post에서 쓰이는 생성자
//        this.orderStatus = OrderStatus.NEW;
//        this.store = orderDto.getStore();
//        this.userName = orderDto.getUserName();
//        this.phoneNumber = orderDto.getPhoneNumber();
//        this.orderTitle = orderDto.getOrderTitle();
//        this.orderTime = orderDto.getOrderTime();
//        this.visitTime = orderDto.getVisitTime();
//        this.orderList = orderDto.getOrderList();
//    }

    public void updateOrder(OrderDto orderDto) {
        this.orderStatus = orderDto.getOrderStatus();
        this.orderList = orderDto.getOrderList();
    }

}
