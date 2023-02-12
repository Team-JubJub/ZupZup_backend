package com.rest.api.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import repository.ItemRepository;
import repository.StoreRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemControllerTest {

    @Autowired private ItemRepository itemRepository;
    @Autowired private StoreRepository storeRepository;

    @Test
    public void 상품저장() {

    }
}
