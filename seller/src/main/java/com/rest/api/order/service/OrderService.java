package com.rest.api.order.service;

import com.rest.api.order.domain.type.OrderSpecific;
import com.rest.api.order.domain.type.OrderStatus;
import com.rest.api.order.exception.RequestedCountExceedStockException;
import com.rest.api.order.repository.OrderRepository;
import com.rest.api.store.domain.Item;
import com.rest.api.store.dto.ItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.rest.api.order.dto.response.OrderResponseDto;
import com.rest.api.order.dto.OrderServiceDto;
import com.rest.api.order.exception.NoSuchException;
import com.rest.api.order.exception.OrderNotInStoreException;
import com.rest.api.order.domain.Order;
import com.rest.api.order.dto.request.OrderRequestDto;
import com.rest.api.store.domain.Store;
import com.rest.api.store.repository.ItemRepository;
import com.rest.api.store.repository.StoreRepository;

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

        List<OrderSpecific> ownerRequestedOrderSpecific = patchOrderDto.getOrderList();  // 사장님이 request한 주문
        List<OrderSpecific> customerRequestedOrderSpecific = orderEntity.getOrderList();    // 여기서부터
        OrderServiceDto orderServiceDto = modelMapper.map(orderEntity, OrderServiceDto.class);
        orderServiceDto.setOrderList(ownerRequestedOrderSpecific);  // 여기까지 괜한 로직이 들어간 것 같은데 해결할 방법 생각해보기.
        int totalOwnerRequestedItemCount = 0; // 주문 취소 여부를 확인 위한 변수. 0일 경우(모든 상품 재고가 없을 경우) 부분확정이 아닌 주문 취소.

        for(int i=0; i < ownerRequestedOrderSpecific.size(); i++) { // 지금은 같은 상품끼리 같은 인덱스일 거라 간주하고 하는데, item id나 이름으로 조회 하는 방법으로 바꿀 것.
            Long ownerRequestedItemId = ownerRequestedOrderSpecific.get(i).getItemId();    // DB Item 개수 변경 위한 Id -> 개발 필요
            int ownerRequestedItemCount = ownerRequestedOrderSpecific.get(i).getItemCount();
            isRequestedCountNotExceedStock(ownerRequestedItemId, ownerRequestedItemCount);  // 상품 재고보다 많은 수의 주문이 확정됐을 시 예외처리

            if(customerRequestedOrderSpecific.get(i).getItemCount() != ownerRequestedItemCount) {  // 사장님이 컨펌한 것과 원래 주문 요청에서의 개수가 하나라도 다르면
                orderServiceDto.getOrderList().get(i).setItemCount(ownerRequestedItemCount);
                orderServiceDto.setOrderStatus(OrderStatus.PARTIAL); // 주문상태 부분확정으로
            }
            updateItemStock(ownerRequestedItemId, ownerRequestedItemCount);
            totalOwnerRequestedItemCount = totalOwnerRequestedItemCount + ownerRequestedItemCount;
        }

        if(totalOwnerRequestedItemCount == 0) {    // 주문 취소
            orderServiceDto.setOrderStatus(OrderStatus.CANCEL);
        }
        else if(orderServiceDto.getOrderStatus() != OrderStatus.PARTIAL) {  // 주문 취소의 경우가 아니고, 부분 확정으로 바뀌지 않은 경우
            orderServiceDto.setOrderStatus(OrderStatus.COMPLETE);   // 주문 확정으로
        }
        orderEntity.updateWhenPatch(orderServiceDto);
        orderRepository.save(orderEntity);

        if(orderEntity.getOrderStatus() == OrderStatus.CANCEL){ // 후에 주문 취소 사유 등 기능이 생기면 DB에 컬럼 추가 및 메서드 생성 후 여기서 수행.
            return "주문이 취소되었습니다.";
        }
        else if(orderEntity.getOrderStatus() == OrderStatus.PARTIAL){
            return "주문이 부분확정되었습니다.";
        }
        return "주문이 확정되었습니다.";
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
    private void updateItemStock(Long ownerRequestedItemId, int ownerRequestedItemCount) {
        ItemDto itemDto = new ItemDto();    // Entity의 개수 변경을 위한 dto
        Item itemEntity = itemRepository.findById(ownerRequestedItemId).get();
        itemDto.setItemCount(itemEntity.getItemCount() - ownerRequestedItemCount);     // 상품 재고에서 요청받은 개수 차감
        itemEntity.updateItemCount(itemDto);
        itemRepository.save(itemEntity);
    }

}
