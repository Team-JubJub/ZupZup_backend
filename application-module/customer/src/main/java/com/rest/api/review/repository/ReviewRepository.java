package com.rest.api.review.repository;

import com.rest.api.review.model.domain.Review;
import com.zupzup.untact.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {

    Page<Review> findAllByProviderUserID(String providerUserID, Pageable pageable); //providerUserID로 작성된 리뷰 찾기
}
