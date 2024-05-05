package com.rest.api.review.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewListResponse {

    private String nickname; // 닉네임
    private float starRate; // 별점
    private String content; // 리뷰
    private String imageURL; // 이미지 URL
    private String menu; // 구매한 메뉴
    private String comment; // 사장님댓글
    private String createdAt; // 리뷰 작성 날짜
}
