package zupzup.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import zupzup.back_end.domain.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {


    //void deleteAllItem(Long storeId);
}
