package repository;


import domain.store.Store;
import domain.store.type.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findBySellerId(Long sellerId);    // 가게 주인 id를 통한 조회
    List<Store> findByCategory(StoreCategory category);    // 카테고리별 조회
    List<Store> findByStoreNameContaining(String keyword);  // 가게 이름으로 검색

}
