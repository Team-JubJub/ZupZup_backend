package com.rest.api.review.service;

import com.rest.api.review.model.dto.ReviewAnnouncementRequest;

public interface ReviewService {

    Long updateReviewAnnouncement(Long storeID, String accessToken, ReviewAnnouncementRequest reviewAnnouncementRequest); // 리뷰 작성, 삭제, 업데이트

}
