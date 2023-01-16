package zupzup.back_end.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;
import zupzup.back_end.store.dto.StoreDto;
import zupzup.back_end.store.repository.ItemRepository;
import zupzup.back_end.store.repository.StoreRepository;

import java.util.List;

@Service
@Log
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    // 상품 리스트
    public List<Item> itemList(StoreDto storeDto) {

        List<Item> itemList = storeDto.getStoreItems();
        return itemList;
    }

    // 가게 메인 페이지
    public StoreDto mainPage(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(EntityNotFoundException::new);

        StoreDto storeDto = new StoreDto();
        storeDto.of(store);

        return storeDto;
    }
}
