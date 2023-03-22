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
    public List<OrderResponseDto.GetOrderDto> orderList(Long storeId) {
        isStorePresent(storeId);    // Check presence of store
        List<Order> allOrderListEntity = orderRepository.findByStore_StoreId(storeId);
        List<OrderResponseDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, OrderResponseDto.GetOrderDto.class))
                .collect(Collectors.toList());

        return allOrderListDto;
    }

    public OrderResponseDto.GetOrderDetailsDto orderDetails(Long storeId, Long orderId) {
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        OrderResponseDto.GetOrderDetailsDto getOrderDetailsDto = modelMapper.map(orderEntity, OrderResponseDto.GetOrderDetailsDto.class);

        return getOrderDetailsDto;
    }

    // <-------------------- PATCH part -------------------->
    public OrderResponseDto.PatchOrderResponseDto updateOrder(Long storeId, Long orderId, OrderRequestDto.PatchOrderDto patchOrderDto) {
        Order orderEntity = exceptionCheckAndGetOrderEntity(storeId, orderId);
        OrderStatus sellerRequestedOrderStatus = patchOrderDto.getOrderStatus(); // 반려, 확정, 취소, 완료
        OrderDto orderDto = modelMapper.map(orderEntity, OrderDto.class);

        if (isOrderCancel(orderEntity, sellerRequestedOrderStatus, orderDto)) { // 반려 or 취소 시.
            orderDto.setOrderStatus(OrderStatus.CANCEL);
            OrderResponseDto.PatchOrderResponseDto patchOrderResponseDto = patchSaveAndReturn(storeId, orderEntity, orderDto);

            return patchOrderResponseDto;
        }

        List<OrderSpecific> customerRequestedOrderList = orderEntity.getOrderList();    // 여기서부터
        List<OrderSpecific> sellerRequestedOrderList = patchOrderDto.getOrderList();  // 사장님이 request한 주문
        orderDto.setOrderList(sellerRequestedOrderList);  // 여기까지 request dto에서 바로 service용 dto 만들 수 있는지 방법 연구
        if(orderEntity.getOrderStatus().equals(OrderStatus.NEW)) {    //신규 주문에 대한 로직(확정)
            for(int i=0; i < sellerRequestedOrderList.size(); i++) { // 지금은 같은 상품끼리 같은 인덱스일 거라 간주하고 하는데, item id나 이름으로 조회 하는 방법으로 바꿀 것.
                Integer sellerRequestedItemCount = sellerRequestedOrderList.get(i).getItemCount();  // 해당 부분 primitive vs wrapper 고민할 것 -> 산술연산 없으므로 wrapper로.
                isRequestedCountNotExceedStock(sellerRequestedOrderList.get(i).getItemId(), sellerRequestedItemCount);  // 상품 재고보다 많은 수의 주문이 확정됐을 시 예외처리
                if(!customerRequestedOrderList.get(i).getItemCount().equals(sellerRequestedItemCount)) {  // 사장님이 컨펌한 것과 원래 주문 요청에서의 개수가 하나라도 다르면
                    orderDto.getOrderList().get(i).setItemCount(sellerRequestedItemCount);
                    orderDto.setOrderStatus(OrderStatus.PARTIAL); // 주문상태 부분확정으로
                }
            }
            if(!orderDto.getOrderStatus().equals(OrderStatus.PARTIAL)) orderDto.setOrderStatus(OrderStatus.CONFIRM);
        }
        else if (orderEntity.getOrderStatus().equals(OrderStatus.CONFIRM) || orderEntity.getOrderStatus().equals(OrderStatus.PARTIAL)){   //확정된 주문에 대한 로직 -> 주문 완료됐으니 재고 수정
//            for(int i=0; i < sellerRequestedOrderList.size(); i++) { //
//                updateItemStock(sellerRequestedOrderList.get(i).getItemId(), sellerRequestedOrderList.get(i).getItemCount()); //재고 수정
//            } -> 사용자 앱으로
            orderDto.setOrderStatus(OrderStatus.COMPLETE);
        }

        OrderResponseDto.PatchOrderResponseDto patchOrderResponseDto = patchSaveAndReturn(storeId, orderEntity, orderDto);
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
        if(!orderEntity.getStore().getStoreId().equals(storeId)) {
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
    private boolean isOrderCancel(Order orderEntity, OrderStatus sellerRequestedOrderStatus, OrderDto orderDto) {
        if(sellerRequestedOrderStatus.equals(OrderStatus.SENDBACK) || sellerRequestedOrderStatus.equals(OrderStatus.CANCEL)) return true;   //신규든 아니든 취소인 경우
        else return false;
    }

//    private void updateItemStock(Long sellerRequestedItemId, int sellerRequestedItemCount) {
//        ItemDto itemDto = new ItemDto();    // Entity의 개수 변경을 위한 dto
//        Item itemEntity = itemRepository.findById(sellerRequestedItemId).get();
//        itemDto.setItemCount(itemEntity.getItemCount() - sellerRequestedItemCount);     // 상품 재고에서 요청받은 개수 차감
//        itemEntity.updateItemCount(itemDto);
//        itemRepository.save(itemEntity);
//    }

    private OrderResponseDto.PatchOrderResponseDto patchSaveAndReturn(Long storeId, Order orderEntity, OrderDto orderDto) {
        orderEntity.updateOrder(orderDto);
        orderRepository.save(orderEntity);
        OrderResponseDto.GetOrderDetailsDto patchedOrderDetailsDto = modelMapper.map(orderEntity, OrderResponseDto.GetOrderDetailsDto.class);
        OrderResponseDto.PatchOrderResponseDto patchOrderResponseDto = new OrderResponseDto.PatchOrderResponseDto();
        patchOrderResponseDto.setData(patchedOrderDetailsDto);
        patchOrderResponseDto.setHref("http://localhost:8080/seller/" + storeId + "/order/" + patchedOrderDetailsDto.getId());

        if(orderEntity.getOrderStatus().equals(OrderStatus.CANCEL)) patchOrderResponseDto.setMessage("주문이 취소되었습니다.");
        else if(orderEntity.getOrderStatus().equals(OrderStatus.CONFIRM)) patchOrderResponseDto.setMessage("주문이 확정되었습니다.");
        else if(orderEntity.getOrderStatus().equals(OrderStatus.PARTIAL)) patchOrderResponseDto.setMessage("주문이 부분확정되었습니다.");
        else if(orderEntity.getOrderStatus().equals(OrderStatus.COMPLETE)) patchOrderResponseDto.setMessage("주문이 완료되었습니다.");

        return patchOrderResponseDto;
    }

}
