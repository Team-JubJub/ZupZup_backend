package com.rest.api.review.controller.impl;

import com.rest.api.review.controller.ReviewController;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.model.dto.ReviewRequest;
import com.rest.api.review.service.ReviewService;
import com.zupzup.untact.social.jwt.SocialJwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @Override
    @PostMapping("")
    public ResponseEntity<Long> save(@RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                               @RequestPart(value = "review") ReviewRequest reviewRequest,
                               @RequestPart(value = "image", required = false) MultipartFile reviewImage) throws Exception {

        // reviewID 리턴
        Long response = reviewService.save(reviewRequest, reviewImage, accessToken);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("")
    public ResponseEntity<List<ReviewListResponse>> findAll(@RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                  @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo) throws Exception {

        List<ReviewListResponse> reviewList = reviewService.findAll(pageNo, accessToken);
        return new ResponseEntity<>(reviewList, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{reviewID}")
    public ResponseEntity<Long> delete(@RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                 @PathVariable Long reviewID) throws Exception {

        Long deletedID = reviewService.delete(reviewID, accessToken);
        return new ResponseEntity<>(deletedID, HttpStatus.OK);
    }

}
