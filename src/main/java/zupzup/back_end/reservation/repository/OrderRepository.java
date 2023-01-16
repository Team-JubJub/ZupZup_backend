package zupzup.back_end.reservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zupzup.back_end.store.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
