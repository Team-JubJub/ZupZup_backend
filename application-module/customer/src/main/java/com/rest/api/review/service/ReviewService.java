package com.rest.api.review.service;

import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.model.dto.ReviewRequest;
import com.rest.api.review.model.dto.ReviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ReviewService {

    ReviewResponse save(ReviewRequest reviewRequest, MultipartFile reviewImage, String providerUserID) throws Exception; // 리뷰 저장
    List<ReviewListResponse> findAll(String providerUserID) throws Exception; // 리뷰 전체보기
}
