package com.rest.api.review.controller;

import com.rest.api.review.model.dto.ReviewAnnouncementRequest;
import com.rest.api.review.model.dto.ReviewCommentRequest;
import org.springframework.http.ResponseEntity;

public interface ReviewController {

    ResponseEntity updateReviewAnnouncement(Long storeID, String accessToken, ReviewAnnouncementRequest reviewAnnouncementRequest); // 리뷰 공지 삭제, 수정
    ResponseEntity writeReviewComment(Long reviewID, String accessToken, ReviewCommentRequest reviewComment); // 리뷰 코멘트 작성
}
