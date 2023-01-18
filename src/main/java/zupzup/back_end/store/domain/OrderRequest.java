package zupzup.back_end.store.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "order_request")
public class OrderRequest {
    //[상품 이름, 가격, 갯수, (이미지)]

    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private int itemPrice;
    private int itemCount;
    private String imgURL;
}
