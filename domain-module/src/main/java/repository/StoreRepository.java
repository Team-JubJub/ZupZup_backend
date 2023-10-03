package repository;


import domain.store.Store;
import domain.store.type.EnterState;
import domain.store.type.StoreCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Store findBySellerId(Long sellerId);    // 가게 주인 id를 통한 조회
    List<Store> findByCategory(StoreCategory category);    // 카테고리별 조회
    List<Store> findByStoreNameContaining(String keyword);  // 가게 이름으로 검색
    List<Store> findByEnterState(EnterState enterState); // 등록 상태로 조회
    @Query(value = "SELECT e FROM Enter e WHERE e.storeName LIKE %:storeName% AND e.state = :state", nativeQuery = true)
    List<Store> searchByStoreNameContainingAndEnterState(@Param("storeName") String storeName, @Param("state") EnterState state); // 가게 이름 및 등록 상태로 조회
}
