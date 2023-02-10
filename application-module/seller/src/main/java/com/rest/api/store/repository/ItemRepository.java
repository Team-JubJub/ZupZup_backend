package com.rest.api.store.repository;

import com.rest.api.store.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rest.api.store.domain.Store;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * storeId 로 스토어 안에 있는 아이템들을 다 가져옴
     */
    List<Item> findAllByStore(Store store);
}
