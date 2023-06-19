package com.rest.api.order.service;

import domain.order.Order;
import domain.order.type.OrderSpecific;
import domain.order.type.OrderStatus;
import domain.store.Store;
import dto.order.OrderDto;
import dto.order.customer.request.OrderRequestDto;
import dto.order.customer.response.OrderResponseDto;
import exception.NoSuchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.ItemRepository;
import repository.OrderRepository;
import repository.StoreRepository;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class OrderService {

    @Autowired
    ModelMapper modelMapper;
    private final StoreRepository storeRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    // <-------------------- POST part -------------------->
    public OrderResponseDto.PostOrderResponseDto addOrder(Long storeId, OrderRequestDto.PostOrderDto postOrderDto) {
        String formattedOrderTime = orderTimeSetter();
        OrderDto orderDto = postOrderDTOtoOrderDTO(storeId, postOrderDto, formattedOrderTime);

        Order orderEntity = Order.builder(orderDto.getStore())
                .orderStatus(OrderStatus.NEW)
                .userName(orderDto.getUserName())
                .phoneNumber(orderDto.getPhoneNumber())
                .orderTitle(orderDto.getOrderTitle())
                .orderTime(orderDto.getOrderTime())
                .visitTime(orderDto.getVisitTime())
                .orderList(orderDto.getOrderList())
                .build();
        orderRepository.save(orderEntity);
        OrderResponseDto.GetOrderDetailsDto madeOrderDetailsDto = modelMapper.map(orderEntity, OrderResponseDto.GetOrderDetailsDto.class);
//        Order orderEntity = new Order(orderDto);  개수 수정 로직 -> 일단 주석처리
//        orderRepository.save(orderEntity);

//        List<OrderSpecific> customerRequestedOrderList = postOrderDto.getOrderList();  // 개수 수정
//        for(int i=0; i < customerRequestedOrderList.size(); i++) { //
//            updateItemStock(customerRequestedOrderList.get(i).getItemId(), customerRequestedOrderList.get(i).getItemCount()); //재고 수정
//        }
        OrderResponseDto.PostOrderResponseDto postOrderResponseDto = new OrderResponseDto.PostOrderResponseDto();
        postOrderResponseDto.setData(madeOrderDetailsDto);
        postOrderResponseDto.setHref("http://localhost:8090/customer/order/"+madeOrderDetailsDto.getOrderId());
        postOrderResponseDto.setMessage("주문이 완료되었습니다.");

        return postOrderResponseDto;
    }

    // <-------------------- GET part -------------------->
    public List<OrderResponseDto.GetOrderDto> orderList() {
        List<Order> allOrderListEntity = orderRepository.findAll();
        List<OrderResponseDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()
            .map(m -> modelMapper.map(m, OrderResponseDto.GetOrderDto.class))
            .collect(Collectors.toList());

        return allOrderListDto;
    }

    public OrderResponseDto.GetOrderDetailsDto orderDetails(Long orderId) {
        Order orderDetailsEntity = isOrderPresent(orderId);
        OrderResponseDto.GetOrderDetailsDto getOrderDetailsDto = modelMapper.map(orderDetailsEntity, OrderResponseDto.GetOrderDetailsDto.class);

        return getOrderDetailsDto;
    }

    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private Order isOrderPresent(Long orderId) {
        try {
            Order orderEntity = orderRepository.findById(orderId).get();
            return orderEntity;
        }   catch (NoSuchElementException e) {
            throw new NoSuchException("해당 주문을 찾을 수 없습니다.");
        }
    }

    // <--- Methods for readability --->
    private String orderTimeSetter() {
        ZonedDateTime nowTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));    // 주문한 시간
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a").withLocale(Locale.ENGLISH);   // 09:43 AM, 04:57 PM
        String formattedOrderTime = nowTime.format(formatter);

        return formattedOrderTime;
    }

    private OrderDto postOrderDTOtoOrderDTO(Long storeId, OrderRequestDto.PostOrderDto postOrderDto, String formattedNowTime) {
        Store store = storeRepository.findById(storeId).get();
        OrderSpecific firstAtOrderSpecific = postOrderDto.getOrderList().get(0);
        String firstAtOrderList = firstAtOrderSpecific.getItemName();
        int firstAtOrderListCount = firstAtOrderSpecific.getItemCount();    // 어차피 String이랑 concat 될 때 int로 unboxing된다고 함. 미리 unboxing.
        int orderListCount = postOrderDto.getOrderList().size() - 1;    // -1이 붙어서 어차피 unboxing 거치니까 int로

        OrderDto orderDto = new OrderDto();
        orderDto.setStore(store);
        orderDto.setOrderStatus(OrderStatus.NEW);
        orderDto.setUserName(postOrderDto.getUserName());
        orderDto.setPhoneNumber(postOrderDto.getPhoneNumber());
        orderDto.setOrderTitle(firstAtOrderList + " " + firstAtOrderListCount + "개 외 " + orderListCount + "건");    // 크로플 3개 외 4건
        orderDto.setOrderTime(formattedNowTime);
        orderDto.setVisitTime(postOrderDto.getVisitTime());
        orderDto.setOrderList(postOrderDto.getOrderList());

        return orderDto;
    }

//    private void updateItemStock(Long customerRequestedItemId, int customerRequestedItemCount) {
//        ItemDto itemDto = new ItemDto();    // Entity의 개수 변경을 위한 dto
//        Item itemEntity = itemRepository.findById(customerRequestedItemId).get();
//        itemDto.setItemCount(itemEntity.getItemCount() - customerRequestedItemCount);     // 상품 재고에서 요청받은 개수 차감
//        itemEntity.updateItemCount(itemDto);
//        itemRepository.save(itemEntity);
//    }

}
