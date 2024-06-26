package com.rest.api.review.model.domain;

import com.zupzup.untact.model.BaseEntity;
import com.zupzup.untact.model.domain.order.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

@Entity
@Getter @Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Where(clause = "is_deleted = 0")
public class Review extends BaseEntity {

    @Column(nullable = false)
    private float starRate; // 별점
    @Column(nullable = false, length = 200)
    private String content; // 리뷰
    @Column(nullable = false)
    private String imageURL; // 이미지 URL
    @Column(length = 300)
    private String comment; // 사장님 댓글

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
    @Column(nullable = false)
    private Long userID; // userID

}
