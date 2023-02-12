package com.rest.api.order.repository;

import domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStore_StoreId(Long storeId);  //해당 store Id를 참조하는 Order의 list
}
