package zupzup.back_end.controller;

import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.repository.ItemRepository;
import zupzup.back_end.repository.StoreRepository;

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
