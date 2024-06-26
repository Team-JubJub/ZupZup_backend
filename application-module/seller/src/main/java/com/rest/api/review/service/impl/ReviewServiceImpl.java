package com.rest.api.review.service.impl;

import com.rest.api.review.exception.ReviewException;
import com.rest.api.review.model.domain.Review;
import com.rest.api.review.model.dto.ReviewAnnouncementRequest;
import com.rest.api.review.model.dto.ReviewCommentRequest;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.repository.ReviewRepository;
import com.rest.api.review.service.ReviewService;
import com.zupzup.untact.custom.jwt.CustomJwtTokenProvider;
import com.zupzup.untact.exception.store.ForbiddenStoreException;
import com.zupzup.untact.exception.store.seller.NoSuchStoreException;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.rest.api.review.exception.ReviewExceptionType.NO_MATCH_REVIEW;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final CustomJwtTokenProvider customJwtTokenProvider;
    private static final int PAGE_SIZE = 20;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public Long updateReviewAnnouncement(Long storeID, String accessToken, ReviewAnnouncementRequest reviewAnnouncementRequest) {

        // accessToken 유효성 검증
        if (customJwtTokenProvider.validateToken(accessToken)) {
            Store store = storeRepository.findById(storeID)
                    .orElseThrow(() -> new NoSuchStoreException("해당 ID의 가게가 존재하지 않습니다."));

            // reviewAnnouncement 내용 확인
            if (reviewAnnouncementRequest.getReviewAnnouncement() == null) {
                // 리뷰 삭제 시 null 설정
                store.setReviewAnnouncement(null);
            } else {
                store.setReviewAnnouncement(reviewAnnouncementRequest.getReviewAnnouncement());
            }
            storeRepository.save(store);

        } else {
            throw new ForbiddenStoreException("해당 가게에 대한 권한이 없습니다.");
        }

        return storeID;
    }

    @Override
    public List<ReviewListResponse> findAll(int pageNo, Long storeID, String accessToken) {

            // accessToken 유효성 검증
            if (customJwtTokenProvider.validateToken(accessToken)) {
                // 가게 아이디로 리뷰 찾기 (Review --> Order --> Store)
                Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<Review> reviews = reviewRepository.findAllByOrder(storeID, pageable);

                return reviews.stream()
                        .map(review -> modelMapper.map(review, ReviewListResponse.class))
                        .collect(Collectors.toList());
            } else {
                throw new ForbiddenStoreException("해당 가게에 대한 권한이 없습니다.");
            }
    }

    @Override
    @Transactional
    public Long writeReviewComment(Long reviewID, String accessToken, ReviewCommentRequest reviewComment) {

        // accessToken 유효성 검증
        if (customJwtTokenProvider.validateToken(accessToken)) {
            Review review = reviewRepository.findById(reviewID)
                    .orElseThrow(() -> new ReviewException(NO_MATCH_REVIEW));

            review.setComment(reviewComment.getReviewComment());

        } else {
            throw new ForbiddenStoreException("해당 가게에 대한 권한이 없습니다.");
        }
        return reviewID;
    }
}
