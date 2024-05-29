package com.rest.api.review.controller.impl;

import com.rest.api.review.controller.ReviewController;
import com.rest.api.review.model.dto.ReviewAnnouncementRequest;
import com.rest.api.review.service.ReviewService;
import com.zupzup.untact.social.jwt.SocialJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @Override
    @PatchMapping("/{storeID}")
    public ResponseEntity<Long>updateReviewAnnouncement(@PathVariable("storeID") Long storeID,
                                                   @RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                                   @RequestBody ReviewAnnouncementRequest reviewAnnouncementRequest) {

        Long result = reviewService.updateReviewAnnouncement(storeID, accessToken, reviewAnnouncementRequest);
        return ResponseEntity.ok(result);
    }
}
