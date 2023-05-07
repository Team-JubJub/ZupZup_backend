package repository;


import domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByLoginId(String loginId);
    List<Store> findByStoreNameContaining(String keyword);
}
