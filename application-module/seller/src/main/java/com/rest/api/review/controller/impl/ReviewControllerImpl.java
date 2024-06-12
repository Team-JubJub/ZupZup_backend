package com.rest.api.review.controller.impl;

import com.rest.api.review.controller.ReviewController;
import com.rest.api.review.model.dto.ReviewAnnouncementRequest;
import com.rest.api.review.model.dto.ReviewCommentRequest;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.service.ReviewService;
import com.zupzup.untact.social.jwt.SocialJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 공지사항 작성, 수정, 삭제
     */
    @Override
    @PatchMapping("/{storeID}")
    public ResponseEntity<Long>updateReviewAnnouncement(@PathVariable("storeID") Long storeID,
                                                   @RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                                   @RequestBody ReviewAnnouncementRequest reviewAnnouncementRequest) {

        Long result = reviewService.updateReviewAnnouncement(storeID, accessToken, reviewAnnouncementRequest);
        return ResponseEntity.ok(result);
    }

    /**
     * 가게 리뷰 전체보기
     */
    @Override
    @GetMapping("/{storeID}")
    public ResponseEntity<List<ReviewListResponse>> findAllReview(@PathVariable("storeID") Long storeID,
                                                        @RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken) {

        List<ReviewListResponse> result = reviewService.findAll(storeID, accessToken);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 리뷰 comment 작성
     */
    @Override
    @PatchMapping("/{reviewID}")
    public ResponseEntity<Long> writeReviewComment(@PathVariable Long reviewID,
                                                   @RequestHeader String accessToken,
                                                   @RequestBody ReviewCommentRequest reviewComment) {

        Long result = reviewService.writeReviewComment(reviewID, accessToken, reviewComment);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
