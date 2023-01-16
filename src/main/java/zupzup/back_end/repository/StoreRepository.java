package zupzup.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zupzup.back_end.domain.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
