package com.rest.api.review.controller.impl;

import com.rest.api.review.controller.ReviewController;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.model.dto.ReviewRequest;
import com.rest.api.review.model.dto.ReviewResponse;
import com.rest.api.review.service.impl.ReviewServiceImpl;
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

    private final ReviewServiceImpl reviewService;

    @Override
    @PostMapping("/{providerUserID}")
    public ResponseEntity save(@RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                               @RequestPart(value = "review") ReviewRequest reviewRequest,
                               @RequestPart(value = "image", required = false) MultipartFile reviewImage,
                               @PathVariable String providerUserID) throws Exception {

        ReviewResponse response = reviewService.save(reviewRequest, reviewImage, providerUserID);

        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @Override
    @GetMapping("/{providerUserID}")
    public ResponseEntity findAll(@RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                  @RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
                                  @PathVariable String providerUserID) throws Exception {
        List<ReviewListResponse> reviewList = reviewService.findAll(pageNo, providerUserID);
        return new ResponseEntity<>(reviewList, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{reviewID}")
    public ResponseEntity delete(@RequestHeader(SocialJwtTokenProvider.ACCESS_TOKEN_NAME) String accessToken,
                                 @PathVariable Long reviewID) throws Exception {
        Long deletedID = reviewService.delete(reviewID);
        return new ResponseEntity<>(deletedID, HttpStatus.OK);
    }

}
