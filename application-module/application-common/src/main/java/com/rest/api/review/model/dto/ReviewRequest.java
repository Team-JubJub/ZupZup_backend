package com.rest.api.review.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewRequest {

    private Long orderID; // 주문 ID
    private String content; // 리뷰
    private float starRate; // 별점
}
