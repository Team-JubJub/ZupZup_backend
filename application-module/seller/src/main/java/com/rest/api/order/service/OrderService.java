package com.rest.api.order.service;

import dto.item.seller.response.GetDtoWithStore;
import dto.order.seller.request.PatchOrderDataDto;
import dto.order.seller.response.GetOrderDetailsDto;
import dto.order.seller.response.GetOrderListDto;
import dto.order.seller.response.PatchOrderResponseDto;
import org.modelmapper.ModelMapper;
import repository.ItemRepository;
import repository.StoreRepository;
import repository.OrderRepository;
import domain.item.Item;
import domain.order.Order;
import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import dto.order.OrderDto;
import exception.NoSuchException;
import exception.OrderNotInStoreException;
import exception.RequestedCountExceedStockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class OrderService {

    @Autowired
    ModelMapper modelMapper;
    private final StoreRepository storeRepository;  // Used for check presence of store(At GET(all) request)
    private final ItemRepository itemRepository;    // Used for patch count of items(At PATCH request)
    private final OrderRepository orderRepository;

    // <-------------------- GET part -------------------->
//    @Cacheable(cacheNames = "sellerOrders", key = "#storeId + #page")    // 리스트 캐시(sellerOrders::storeId+pageNo 형식, 페이지 별로 캐시함.) -> 캐시 관련한 것 일단 사용자 앱 만들어지기 전까지 주석처리
    public GetOrderListDto orderList(Long storeId) {
        isStorePresent(storeId);    // Check presence of store

        List<Order> allOrderListEntity = orderRepository.findByStoreId(storeId);
        List<GetOrderDetailsDto> orderList = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, GetOrderDetailsDto.class))
                .collect(Collectors.toList());
        GetOrderListDto getOrderListDto = new GetOrderListDto();
        getOrderListDto.setOrderList(orderList);

        return getOrderListDto;
    }

    public GetOrderDetailsDto orderDetails(Long storeId, Long orderId) {
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        GetOrderDetailsDto getOrderDetailsDto = modelMapper.map(orderEntity, GetOrderDetailsDto.class);

        return getOrderDetailsDto;
    }

    // <-------------------- PATCH part -------------------->
    //    @CacheEvict(cacheNames = "sellerOrders", allEntries = true) // 주문 정보 수정 시 모든 orderList 페이지의 캐시 삭제. -> 캐시 관련한 것 일단 사용자 앱 만들어지기 전까지 주석처리
    public PatchOrderResponseDto updateOrderStatus(Long storeId, Long orderId, OrderStatus requestedOrderStatus) {   // 신규 주문 취소 시, 확정 주문 완료 시
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        OrderDto orderDto = modelMapper.map(orderEntity, OrderDto.class);
        orderDto.setOrderStatus(requestedOrderStatus);  // 신규 주문 취소 시 = CANCEL, 확정 주문 완료 시 = COMPLETE -> 한 함수에서 다 처리하려고 했으나, CANCEL의 신규 주문과 확정 주문 여부를 확인할 방법이 없음.
        PatchOrderResponseDto patchOrderResponseDto = updateOrderAndReturn(orderEntity, orderDto);

        return patchOrderResponseDto;
    }

    public PatchOrderResponseDto updateOrderData(Long storeId, Long orderId, PatchOrderDataDto patchOrderDataDto, OrderStatus requestedOrderStatus) { // 신규 주문 확정 시, 확정 주문 취소 시
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        PatchOrderResponseDto patchOrderResponseDto = updateOrderDataAndReturn(orderEntity, patchOrderDataDto, requestedOrderStatus);

        return patchOrderResponseDto;
    }


    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private void isStorePresent(Long storeId) {
        try {
            Store storeEntity = storeRepository.findById(storeId).get();    // 이 부분 entity 안받아와도 할 수 있는 방법 있는지 찾아볼 것.
            System.out.println("Store Found with ID: " + storeId + ", name: " + storeEntity.getStoreName());    // 확인용
        }   catch (NoSuchElementException e) {
            throw new NoSuchException("등록되지 않은 가게입니다.");
        }
    }

    private Order isOrderPresent(Long orderId) {
        try {
            Order orderEntity = orderRepository.findById(orderId).get();
            return orderEntity;
        }   catch (NoSuchElementException e) {
            throw new NoSuchException("해당 주문을 찾을 수 없습니다.");
        }
    }

    private void isOrderInStore(Long storeId, Order orderEntity) {
        if(!orderEntity.getStoreId().equals(storeId)) {
            throw new OrderNotInStoreException();
        }
    }

    private Order exceptionCheckAndGetOrderEntity(Long storeId, Long orderId) {
        Order orderEntity = isOrderPresent(orderId);
        isOrderInStore(storeId, orderEntity);
        return orderEntity;
    }

    private void isRequestedCountNotExceedStock(Long sellerRequestedItemId, Integer sellerRequestedItemCount) {
        Item itemEntity = itemRepository.findById(sellerRequestedItemId).get();
        if(sellerRequestedItemCount.compareTo(itemEntity.getItemCount()) > 0) { // sellerReq - 가 itemEntity.get-보다 크면
            throw new RequestedCountExceedStockException(itemEntity.getItemId(), itemEntity.getItemName());
        }
    }

    // <--- Methods for readability --->
    private void updateItemStock(OrderStatus orderStatus, List<OrderSpecific> orderList) {  // 주문 후 아이템 재고를 수정하는 함수
        for (int i = 0; i < orderList.size(); i++) {
            Long itemId = orderList.get(i).getItemId();
            int orderedItemCount = orderList.get(i).getItemCount(); // 여기까지 클라에서 받아온 아이템 주문 정보

            GetDtoWithStore itemDto = new GetDtoWithStore();    // Entity 수정에 쓰일 Dto.
            Item itemEntity = itemRepository.findById(itemId).get();
            int originalItemCount = itemEntity.getItemCount();

            if (orderStatus.equals(OrderStatus.CONFIRM)) {
                isRequestedCountNotExceedStock(itemId, orderedItemCount);   // 재고보다 많은 양의 주문인지 체크
                itemDto.setItemCount(originalItemCount - orderedItemCount);    // 주문 확정 시에는 뺴주고
            }
            else if (orderStatus.equals(OrderStatus.CANCEL)) itemDto.setItemCount(originalItemCount + orderedItemCount); // 주문 취소 시에는 더해줌.
            itemEntity.updateItemCount(itemDto);
            itemRepository.save(itemEntity);
        }
    }

    private PatchOrderResponseDto saveUpdatedDataAndReturnResponse(Order orderEntity, OrderDto orderDto) {
        orderEntity.updateOrder(orderDto);
        orderRepository.save(orderEntity);
        GetOrderDetailsDto patchedOrderDetailsDto = modelMapper.map(orderEntity, GetOrderDetailsDto.class);
        PatchOrderResponseDto patchOrderResponseDto = new PatchOrderResponseDto();
        patchOrderResponseDto.setData(patchedOrderDetailsDto);
        return patchOrderResponseDto;
    }

    private PatchOrderResponseDto updateOrderAndReturn(Order orderEntity, OrderDto orderDto) { // 신규 주문 취소, 확정 주문 완료일 때도 같이 처리함
        PatchOrderResponseDto patchOrderResponseDto = saveUpdatedDataAndReturnResponse(orderEntity, orderDto);

        if(orderEntity.getOrderStatus().equals(OrderStatus.CANCEL)) patchOrderResponseDto.setMessage("주문이 취소되었습니다.");   // 주문 취소일 떄
        else if(orderEntity.getOrderStatus().equals(OrderStatus.COMPLETE)) patchOrderResponseDto.setMessage("주문이 완료되었습니다.");    // 주문 완료일 때
        else if(orderEntity.getOrderStatus().equals(OrderStatus.CONFIRM)) patchOrderResponseDto.setMessage("주문이 확정되었습니다."); // 주문 확정일 떄

        return patchOrderResponseDto;
    }

    private PatchOrderResponseDto updateOrderDataAndReturn(Order orderEntity, PatchOrderDataDto patchOrderDataDto, OrderStatus sellerRequestedOrderStatus) {  // 신규 주문 확정, 확정 주문 취소에 대해 처리하는 함수
        List<OrderSpecific> orderList = patchOrderDataDto.getOrderList();

        OrderDto orderDto = modelMapper.map(orderEntity, OrderDto.class);
        orderDto.setOrderStatus(sellerRequestedOrderStatus);    // CONFIRM or CANCEL
        orderDto.setOrderList(orderList);

        updateItemStock(sellerRequestedOrderStatus, orderList); // 주문한 개수만큼 재고에서 차감, 더하기
        PatchOrderResponseDto patchOrderResponseDto = updateOrderAndReturn(orderEntity, orderDto);

        return patchOrderResponseDto;
    }

}
