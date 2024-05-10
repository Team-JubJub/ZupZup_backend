package com.rest.api.review.controller;

import com.rest.api.review.model.dto.ReviewRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public interface ReviewController {

    ResponseEntity save(String accessToken, ReviewRequest reviewRequest, MultipartFile reviewImage) throws Exception; // 리뷰 저장
    ResponseEntity findAll(String accessToken, int pageNo) throws Exception; // 리뷰 전체보기
    ResponseEntity delete(String accessToken, Long reviewID) throws Exception; // 리뷰 삭제
}
