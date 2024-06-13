package com.rest.api.review.repository;

import com.rest.api.review.model.domain.Review;
import com.zupzup.untact.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends BaseRepository<Review> {

    Page<Review> findAllByUserID(Long userID, Pageable pageable); //providerUserID로 작성된 리뷰 찾기
    @Query("SELECT r FROM Review r WHERE r.order.storeId = :storeId")
    Page<Review> findAllByOrder(Long storeId, Pageable pageable); //store 리뷰 찾기

}
