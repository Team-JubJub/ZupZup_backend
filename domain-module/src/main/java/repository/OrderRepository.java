package repository;

import domain.order.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStoreId(Long storeId);  //해당 store Id를 참조하는 Order의 list
    List<Order> findByUserId(Long userId);  // 해당 user Id를 참조하는 Order의 list

}
