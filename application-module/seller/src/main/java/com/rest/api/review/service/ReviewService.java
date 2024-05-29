package com.rest.api.review.service;

import com.rest.api.review.model.dto.ReviewAnnouncementRequest;
import com.rest.api.review.model.dto.ReviewCommentRequest;

public interface ReviewService {

    // 리뷰 공지 작성, 삭제, 업데이트
    Long updateReviewAnnouncement(Long storeID, String accessToken, ReviewAnnouncementRequest reviewAnnouncementRequest);
    // 사용자 리뷰 코멘트 작성
    Long writeReviewComment(Long reviewID, String accessToken, ReviewCommentRequest reviewComment);
    // 가게 전체 리뷰 조회
}
