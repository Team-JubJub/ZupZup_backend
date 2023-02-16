package com.rest.api.order.service;


import org.modelmapper.ModelMapper;
import repository.ItemRepository;
import repository.StoreRepository;
import repository.OrderRepository;
import domain.item.Item;
import domain.order.Order;
import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import dto.item.ItemDto;
import dto.order.OrderServiceDto;
import dto.order.seller.request.OrderRequestDto;
import dto.order.seller.response.OrderResponseDto;
import exception.seller.order.NoSuchException;
import exception.seller.order.OrderNotInStoreException;
import exception.seller.order.RequestedCountExceedStockException;
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
    public List<OrderResponseDto.GetOrderDto> getAllOrder(Long storeId) {
        isStorePresent(storeId);    // Check presence of store
        List<Order> allOrderListEntity = orderRepository.findByStore_StoreId(storeId);
        List<OrderResponseDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, OrderResponseDto.GetOrderDto.class))
                .collect(Collectors.toList());

        return allOrderListDto;
    }

    public OrderResponseDto.GetOrderSpecificDto getOrderSpecificById(Long storeId, Long orderId) {
        Order orderEntity = isOrderPresent(orderId);
        isOrderInStore(storeId, orderEntity);
        OrderResponseDto.GetOrderSpecificDto getOrderSpecificDto = modelMapper.map(orderEntity, OrderResponseDto.GetOrderSpecificDto.class);

        return getOrderSpecificDto;
    }

    // <-------------------- PATCH part -------------------->
    public String patchOrderById(Long storeId, Long orderId, OrderRequestDto.PatchOrderDto patchOrderDto) {
        Order orderEntity = isOrderPresent(orderId);
        isOrderInStore(storeId, orderEntity);

        OrderStatus ownerRequestedOrderStatus = patchOrderDto.getOrderStatus(); // 반려, 확정, 취소, 완료
        List<OrderSpecific> ownerRequestedOrderSpecific = patchOrderDto.getOrderList();  // 사장님이 request한 주문
        List<OrderSpecific> customerRequestedOrderSpecific = orderEntity.getOrderList();    // 여기서부터
        OrderServiceDto orderServiceDto = modelMapper.map(orderEntity, OrderServiceDto.class);
        orderServiceDto.setOrderList(ownerRequestedOrderSpecific);  // 여기까지 request dto에서 바로 service용 dto 만들 수 있는지 방법 연구
        if (isOrderCancel(orderEntity, ownerRequestedOrderStatus, orderServiceDto)) return "주문이 취소되었습니다.";

        if(orderEntity.getOrderStatus() == OrderStatus.NEW) {    //신규 주문에 대한 로직(확정)
            for(int i=0; i < ownerRequestedOrderSpecific.size(); i++) { // 지금은 같은 상품끼리 같은 인덱스일 거라 간주하고 하는데, item id나 이름으로 조회 하는 방법으로 바꿀 것.
                Long ownerRequestedItemId = ownerRequestedOrderSpecific.get(i).getItemId();    // DB Item 개수 변경 위한 Id
                int ownerRequestedItemCount = ownerRequestedOrderSpecific.get(i).getItemCount();
                isRequestedCountNotExceedStock(ownerRequestedItemId, ownerRequestedItemCount);  // 상품 재고보다 많은 수의 주문이 확정됐을 시 예외처리

                if(customerRequestedOrderSpecific.get(i).getItemCount() != ownerRequestedItemCount) {  // 사장님이 컨펌한 것과 원래 주문 요청에서의 개수가 하나라도 다르면
                    orderServiceDto.getOrderList().get(i).setItemCount(ownerRequestedItemCount);
                    orderServiceDto.setOrderStatus(OrderStatus.PARTIAL); // 주문상태 부분확정으로
                }
            }
            if(orderServiceDto.getOrderStatus() != OrderStatus.PARTIAL) {
                orderServiceDto.setOrderStatus(OrderStatus.CONFIRM);
            }
        }
        else {   //신규 주문 이외의 주문(확정된 주문)에 대한 로직 -> 주문 완료됐으니 재고 수정
            for(int i=0; i < ownerRequestedOrderSpecific.size(); i++) { //
                Long ownerRequestedItemId = ownerRequestedOrderSpecific.get(i).getItemId();    // DB Item 개수 변경 위한 Id
                int ownerRequestedItemCount = ownerRequestedOrderSpecific.get(i).getItemCount();
                updateItemStock(ownerRequestedItemId, ownerRequestedItemCount); //재고 수정
            }
            orderServiceDto.setOrderStatus(OrderStatus.COMPLETE);
        }
        String patchResult = patchSaveAndReturn(orderEntity, orderServiceDto);
        return patchResult;
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
        if(orderEntity.getStore().getStoreId() != storeId){
            throw new OrderNotInStoreException();
        }
    }

    private void isRequestedCountNotExceedStock(Long ownerRequestedItemId, int ownerRequestedItemCount) {
        Item itemEntity = itemRepository.findById(ownerRequestedItemId).get();
        if(ownerRequestedItemCount > itemEntity.getItemCount()) {
            throw new RequestedCountExceedStockException(itemEntity.getItemId(), itemEntity.getItemName());
        }
    }

    // <--- Methods for readability --->
    private boolean isOrderCancel(Order orderEntity, OrderStatus ownerRequestedOrderStatus, OrderServiceDto orderServiceDto) {
        if(ownerRequestedOrderStatus == OrderStatus.SENDBACK || ownerRequestedOrderStatus == OrderStatus.CANCEL) { //신규든 아니든 취소인 경우
            orderServiceDto.setOrderStatus(OrderStatus.CANCEL);
            orderEntity.updateWhenPatch(orderServiceDto);
            orderRepository.save(orderEntity);
            return true;
        }
        return false;
    }

    private void updateItemStock(Long ownerRequestedItemId, int ownerRequestedItemCount) {
        ItemDto itemDto = new ItemDto();    // Entity의 개수 변경을 위한 dto
        Item itemEntity = itemRepository.findById(ownerRequestedItemId).get();
        itemDto.setItemCount(itemEntity.getItemCount() - ownerRequestedItemCount);     // 상품 재고에서 요청받은 개수 차감
        itemEntity.updateItemCount(itemDto);
        itemRepository.save(itemEntity);
    }

    private String patchSaveAndReturn(Order orderEntity, OrderServiceDto orderServiceDto) {
        orderEntity.updateWhenPatch(orderServiceDto);
        orderRepository.save(orderEntity);
        if(orderEntity.getOrderStatus() == OrderStatus.CONFIRM) return "주문이 확정되었습니다.";
        else if(orderEntity.getOrderStatus() == OrderStatus.PARTIAL) return "주문이 부분확정되었습니다.";
        else if(orderEntity.getOrderStatus() == OrderStatus.COMPLETE) return "주문이 완료되었습니다.";
        return "주문이 완료되었습니다.";
    }

}
