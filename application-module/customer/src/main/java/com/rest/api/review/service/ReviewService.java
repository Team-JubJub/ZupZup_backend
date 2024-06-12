package com.rest.api.review.service;

import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.model.dto.ReviewRequest;
import com.rest.api.review.model.dto.ReviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ReviewService {

    Long save(ReviewRequest reviewRequest, MultipartFile reviewImage, String accessToken) throws Exception; // 리뷰 저장
    List<ReviewListResponse> findAll(int pageNo, String accessToken) throws Exception; // 리뷰 전체보기
    Long delete(Long reviewID, String accessToken) throws Exception; // 리뷰 삭제
}
