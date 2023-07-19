package com.rest.api.order.service;


import dto.item.ItemDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import repository.ItemRepository;
import repository.StoreRepository;
import repository.OrderRepository;
import domain.item.Item;
import domain.order.Order;
import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import dto.order.OrderDto;
import dto.order.seller.request.OrderRequestDto;
import dto.order.seller.response.OrderResponseDto;
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
    public OrderResponseDto.GetOrderListDto orderList(Long storeId, int page, Pageable pageable) {
        isStorePresent(storeId);    // Check presence of store
        Boolean hasNext = true; // 다음 페이지가 있는지 여부를 판단하는 변수

        List<Order> allOrderListEntity = orderRepository.findByStoreId(storeId, pageable);
        List<OrderResponseDto.GetOrderDetailsDto> orderList = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, OrderResponseDto.GetOrderDetailsDto.class))
                .collect(Collectors.toList());
        if (orderList.size() == 0) {    // 없는 페이지를 조회했을 경우
            hasNext = false;
        } else if (orderList.get(orderList.size() - 1).getOrderId() == 1) {    // 해당 페이지의 마지막 주문의 id가 1이면
            hasNext = false;
        }
        OrderResponseDto.GetOrderListDto getOrderListDto = new OrderResponseDto.GetOrderListDto();
        getOrderListDto.setOrderList(orderList);
        getOrderListDto.setPageNo(page);
        getOrderListDto.setHasNext(hasNext);

        return getOrderListDto;
    }

    public OrderResponseDto.GetOrderDetailsDto orderDetails(Long storeId, Long orderId) {
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        OrderResponseDto.GetOrderDetailsDto getOrderDetailsDto = modelMapper.map(orderEntity, OrderResponseDto.GetOrderDetailsDto.class);

        return getOrderDetailsDto;
    }

    // <-------------------- PATCH part -------------------->
//    @CacheEvict(cacheNames = "sellerOrders", allEntries = true) // 주문 정보 수정 시 모든 orderList 페이지의 캐시 삭제. -> 캐시 관련한 것 일단 사용자 앱 만들어지기 전까지 주석처리
    public OrderResponseDto.PatchOrderStatusResponseDto cancelNewOrder(Long storeId, Long orderId/*, OrderRequestDto.PatchOrderDto patchOrderDto*/) {
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        OrderDto orderDto = modelMapper.map(orderEntity, OrderDto.class);
        orderDto.setOrderStatus(OrderStatus.CANCEL);  // 주문 취소 시
        OrderResponseDto.PatchOrderStatusResponseDto patchOrderStatusResponseDto = statusSaveAndReturn(storeId, orderEntity, orderDto);

        return patchOrderStatusResponseDto;
    }

    public OrderResponseDto.PatchOrderDataResponseDto confirmNewOrder(Long storeId, Long orderId/*, OrderRequestDto.PatchOrderDto patchOrderDto*/) {
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        OrderDto orderDto = modelMapper.map(orderEntity, OrderDto.class);
        OrderResponseDto.PatchOrderDataResponseDto patchOrderDataResponseDto = updateItemStockAndOrderStatus(storeId, orderEntity, OrderStatus.CONFIRM, orderDto);

        return patchOrderDataResponseDto;
    }

    public OrderResponseDto.PatchOrderDataResponseDto completeOrder(Long storeId, Long orderId, OrderRequestDto.PatchOrderDto patchOrderDto) {
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);  // 원래 주문의 정보
        OrderStatus sellerRequestedOrderStatus = patchOrderDto.getOrderStatus(); // 완료, 취소
        OrderDto orderDto = modelMapper.map(orderEntity, OrderDto.class);   // 원래 주문 정보의 dto
        if (sellerRequestedOrderStatus.equals(OrderStatus.CANCEL)) { // 주문 취소 시
            OrderResponseDto.PatchOrderDataResponseDto patchOrderDataResponseDto = updateItemStockAndOrderStatus(storeId, orderEntity, sellerRequestedOrderStatus, orderDto);
            return patchOrderDataResponseDto;
        }

        orderDto.setOrderStatus(OrderStatus.COMPLETE);  // 주문 완료 시
        OrderResponseDto.PatchOrderDataResponseDto patchOrderDataResponseDto = dataSaveAndReturn(storeId, orderEntity, orderDto);

        return patchOrderDataResponseDto;
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

            ItemDto.getDtoWithStore itemDto = new ItemDto.getDtoWithStore();    // Entity 수정에 쓰일 Dto.
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

    private OrderResponseDto.PatchOrderDataResponseDto updateItemStockAndOrderStatus(Long storeId, Order orderEntity, OrderStatus sellerRequestedOrderStatus, OrderDto orderDto) {
        List<OrderSpecific> orderList = orderDto.getOrderList();
        updateItemStock(sellerRequestedOrderStatus, orderList); // 주문한 개수만큼 재고에서 차감
        orderDto.setOrderStatus(OrderStatus.CONFIRM);
        OrderResponseDto.PatchOrderDataResponseDto patchOrderDataResponseDto = dataSaveAndReturn(storeId, orderEntity, orderDto);

        return patchOrderDataResponseDto;
    }

    private OrderResponseDto.PatchOrderStatusResponseDto statusSaveAndReturn(Long storeId, Order orderEntity, OrderDto orderDto) {
        orderEntity.updateOrder(orderDto);
        orderRepository.save(orderEntity);
        OrderStatus changedOrderStatus = orderEntity.getOrderStatus();

        OrderResponseDto.PatchOrderStatusResponseDto patchOrderStatusResponseDto = new OrderResponseDto.PatchOrderStatusResponseDto();
        patchOrderStatusResponseDto.setData(modelMapper.map(orderEntity, OrderResponseDto.GetOrderDetailsDto.class));
        if (changedOrderStatus.getStatus().equals(OrderStatus.CANCEL)) patchOrderStatusResponseDto.setMessage("주문이 취소되었습니다.");  // 신규 주문 취소일 떄
        else patchOrderStatusResponseDto.setMessage("주문이 완료되었습니다.");    // 확정 주문 완료일 때

        return patchOrderStatusResponseDto;
    }
    private OrderResponseDto.PatchOrderDataResponseDto dataSaveAndReturn(Long storeId, Order orderEntity, OrderDto orderDto) {
        orderEntity.updateOrder(orderDto);
        orderRepository.save(orderEntity);
        OrderResponseDto.GetOrderDetailsDto patchedOrderDetailsDto = modelMapper.map(orderEntity, OrderResponseDto.GetOrderDetailsDto.class);
        OrderResponseDto.PatchOrderDataResponseDto patchOrderDataResponseDto = new OrderResponseDto.PatchOrderDataResponseDto();
        patchOrderDataResponseDto.setData(patchedOrderDetailsDto);

        if(orderEntity.getOrderStatus().equals(OrderStatus.CANCEL)) patchOrderDataResponseDto.setMessage("주문이 취소되었습니다.");
        else if(orderEntity.getOrderStatus().equals(OrderStatus.CONFIRM)) patchOrderDataResponseDto.setMessage("주문이 확정되었습니다.");

        return patchOrderDataResponseDto;
    }

}
