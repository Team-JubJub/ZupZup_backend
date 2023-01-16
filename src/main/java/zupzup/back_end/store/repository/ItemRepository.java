package zupzup.back_end.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zupzup.back_end.store.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {


    //void deleteAllItem(Long storeId);
}
