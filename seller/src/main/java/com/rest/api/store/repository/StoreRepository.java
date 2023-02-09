package com.rest.api.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rest.api.store.domain.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
