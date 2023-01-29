package zupzup.back_end.store.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.store.domain.Item;
import zupzup.back_end.store.domain.Store;
import zupzup.back_end.store.dto.ItemDto;
import zupzup.back_end.store.dto.StoreDto;
import zupzup.back_end.store.dto.response.ItemResponseDto;
import zupzup.back_end.store.dto.response.StoreResponseDto;
import zupzup.back_end.store.repository.ItemRepository;
import zupzup.back_end.store.repository.StoreRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
@RequiredArgsConstructor
@Transactional
public class StoreService {

    private final StoreRepository storeRepository;
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    // 가게 저장

    // 가게 메인 페이지
    public StoreResponseDto mainPage(Long storeId) {

        // 가게 관련 내용 (가게 이름 및 운영 시간, 이벤트 내용, 오늘 할인 시간)
        // 제품 관련 내용 ([제품 이미지, 제품 이름, 가격])

        Store store = storeRepository.findById(storeId).get();
        StoreResponseDto responseDto = new StoreResponseDto();

        // 엔티티->Dto
        responseDto.setStoreId(store.getStoreId());
        responseDto.setStoreName(store.getStoreName());
        responseDto.setOpenTime(store.getOpenTime());
        responseDto.setEndTime(store.getEndTime());
        responseDto.setEventList(store.getEventList());
        responseDto.setSaleTimeStart(store.getSaleTimeStart());
        responseDto.setSaleTimeEnd(store.getSaleTimeEnd());

        // 아이템 가져오기 및 저장
        List<Item> itemList = itemRepository.findAllByStore(store);
        List<ItemResponseDto> itemDtoList = new ArrayList<>();

        for(Item item : itemList) {

            ItemResponseDto itemDto = new ItemResponseDto();
            itemDtoList.add(itemDto.toItemResponseDto(item));
        }

        responseDto.setStoreItems(itemDtoList);

        return responseDto;
    }
}
