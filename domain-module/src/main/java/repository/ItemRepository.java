package repository;

import domain.item.Item;
import domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * storeId 로 스토어 안에 있는 아이템들을 다 가져옴
     */
    List<Item> findAllByStore(Store store);
}
