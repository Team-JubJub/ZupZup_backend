package com.rest.api.review.model.domain;

import com.zupzup.untact.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private String nickname; // 닉네임
    @Column(nullable = false)
    private float starRate; // 별점
    @Column(nullable = false, length = 200)
    private String content; // 리뷰
    @Column(nullable = false)
    private String imageURL; // 이미지 URL
    @Column(length = 300)
    private String comment; // 사장님 댓글
    @Column(nullable = false)
    private String menu; // 구매한 메뉴

    @Column(nullable = false)
    private Long orderID; // order ID
    @Column(nullable = false)
    private String providerUserID; // userID

}
