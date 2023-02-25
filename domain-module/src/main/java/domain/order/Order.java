package domain.order;

import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import dto.order.OrderDto;
import jakarta.persistence.*;
import lombok.Getter;


import java.util.List;

@Entity
@Getter
@Table(name = "orders")  // table name이 order -> SQL 예약어와 동일, table 명 수정.
public class Order {

    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "storeId")
    private Store store;    // store (relation with store table)
    
    private OrderStatus orderStatus;    // 주문 상태
    private String userName; // 예약자명
    private String phoneNumber; // 예약자 전화번호
    private String orderTitle; // ex) 크로플 3개 외 3
    private String orderTime; // 주문 시간 -> ERD에 추가
    private String visitTime; // 방문예정 시간

    @ElementCollection
    @CollectionTable(name = "orderSpecific", joinColumns = @JoinColumn(name="orderId", referencedColumnName="orderId"))
    private List<OrderSpecific> orderList;  // 주문 품목(이름, 가격, 개수, (img)

    public void addOrder(OrderDto orderDto) {
        this.orderStatus = OrderStatus.NEW;
        this.store = orderDto.getStore();
        this.userName = orderDto.getUserName();
        this.phoneNumber = orderDto.getPhoneNumber();
        this.orderTitle = orderDto.getOrderTitle();
        this.orderTime = orderDto.getOrderTime();
        this.visitTime = orderDto.getVisitTime();
        this.orderList = orderDto.getOrderList();
    }

    public void updateOrder(OrderDto orderDto) {
        this.orderStatus = orderDto.getOrderStatus();
        this.orderList = orderDto.getOrderList();
    }

}
