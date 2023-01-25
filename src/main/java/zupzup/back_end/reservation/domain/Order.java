package zupzup.back_end.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import zupzup.back_end.reservation.domain.type.OrderSpecific;
import zupzup.back_end.store.domain.Store;
import zupzup.back_end.reservation.domain.type.OrderStatus;

import java.util.List;

@Entity
@Getter
@Table(name = "orders")  // table name이 order -> SQL 예약어와 동일, table 명 수정.
public class Order {

    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Store store;    // store (relation with store table)

    private OrderStatus orderStatus; // 상태여부 -> ERD에 추가
    private String username; // 예약자명
    private String phoneNumber; // 예약자 전화번호
    private String orderTitle; // ex) 크로플 3개 외 3
    private String orderTime; // 주문 시간 -> ERD에 추가
    private String visitTime; // 방문예정 시간

    @ElementCollection
    @CollectionTable(name="orderSpecific")
    private List<OrderSpecific> orderSpecificList;  // 주문 품목(이름, 가격, 개수, (img)

}
