package zupzup.back_end.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import zupzup.back_end.reservation.domain.type.OrderStatus;

import java.util.List;

@Entity
@Getter
@Table(name = "order")
@NoArgsConstructor
public class Order {

    @Id
    @Column(name = "orderId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Store store;

    private String username; // 닉네임
    private String phoneNum; // 전화번호
    private String visitTime; // 방문예정 시간
    private OrderStatus orderStatus; // 상태여부
    private int count; // 상품 총 개수

    @NotNull
    @OneToMany(fetch = FetchType.LAZY)
    private List<OrderRequest> orderList; // 주문 리스트

}
