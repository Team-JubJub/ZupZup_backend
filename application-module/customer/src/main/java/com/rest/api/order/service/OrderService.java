package com.rest.api.order.service;

import com.rest.api.FCM.dto.FCMAlertDto;
import com.rest.api.FCM.service.FCMService;
import com.rest.api.utils.AuthUtils;
import com.zupzup.untact.domain.auth.user.User;
import com.zupzup.untact.domain.data.FirstOrderData;
import com.zupzup.untact.domain.order.Order;
import com.zupzup.untact.domain.order.type.OrderSpecific;
import com.zupzup.untact.domain.order.type.OrderStatus;
import com.zupzup.untact.domain.store.Store;
import com.zupzup.untact.dto.order.OrderDto;
import com.zupzup.untact.dto.order.customer.request.PostOrderRequestDto;
import com.zupzup.untact.dto.order.customer.response.GetOrderDetailsDto;
import com.zupzup.untact.dto.order.customer.response.GetOrderDto;
import com.zupzup.untact.dto.order.customer.response.PostOrderResponseDto;
import com.zupzup.untact.repository.FirstOrderDataRepository;
import com.zupzup.untact.repository.OrderRepository;
import com.zupzup.untact.repository.StoreRepository;
import exception.NoSuchException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final FirstOrderDataRepository firstOrderDataRepository;
    private final AuthUtils authUtils;
    private final FCMService fcmService;

    // <-------------------- POST part -------------------->
    public PostOrderResponseDto addOrder(String accessToken, Long storeId, PostOrderRequestDto postOrderRequestDto) {
        User userEntity = authUtils.getUserEntity(accessToken);
        String formattedOrderTime = orderTimeSetter();
        OrderDto orderDto = postOrderDTOtoOrderDTO(userEntity, storeId, postOrderRequestDto, formattedOrderTime);
        if (userEntity.getOrderCount() == 0) saveFirstOrderData(userEntity.getRegisterTime(), orderDto.getOrderTime());

        Order orderEntity = Order.builder(orderDto.getStoreId())
                .userId(orderDto.getUserId()) // user id 테스트 값임
                .orderStatus(OrderStatus.NEW)
                .userName(orderDto.getUserName())
                .phoneNumber(orderDto.getPhoneNumber())
                .orderTitle(orderDto.getOrderTitle())
                .orderTime(orderDto.getOrderTime())
                .visitTime(orderDto.getVisitTime())
                .storeName(orderDto.getStoreName())
                .storeAddress(orderDto.getStoreAddress())
                .storeContact(orderDto.getStoreContact())
                .category(orderDto.getCategory())
                .orderList(orderDto.getOrderList())
                .totalPrice(orderDto.getTotalPrice())
                .savedMoney(orderDto.getSavedMoney())
                .build();
        orderRepository.save(orderEntity);
        sendMessage(storeId, "신규 주문 접수", "신규 주문(" + orderDto.getOrderTitle() + ")이 접수되었습니다.");  // 푸시 알림 보내기

        GetOrderDetailsDto madeOrderDetailsDto = modelMapper.map(orderEntity, GetOrderDetailsDto.class);
        PostOrderResponseDto postOrderResponseDto = new PostOrderResponseDto();
        postOrderResponseDto.setData(madeOrderDetailsDto);
        postOrderResponseDto.setHref(":8090/order/"+madeOrderDetailsDto.getOrderId());
        postOrderResponseDto.setMessage("주문이 완료되었습니다.");

        return postOrderResponseDto;
    }

    // <-------------------- GET part -------------------->
    public List<GetOrderDto> orderList(String accessToken) {
        User userEntity = authUtils.getUserEntity(accessToken);
        List<Order> userOrderListEntity = orderRepository.findByUserId(userEntity.getUserId());
        List<GetOrderDto> userOrderListDto = userOrderListEntity.stream()
                .filter(m -> !m.getOrderStatus().equals(OrderStatus.WITHDREW))
                .map(m -> modelMapper.map(m, GetOrderDto.class))
                .collect(Collectors.toList());

        return userOrderListDto;
    }

    public GetOrderDetailsDto orderDetails(Long orderId) {
        Order orderDetailsEntity = isOrderPresent(orderId);
        GetOrderDetailsDto getOrderDetailsDto = modelMapper.map(orderDetailsEntity, GetOrderDetailsDto.class);

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");   // ex) 2023-07-26 21:54
        String formattedOrderTime = nowTime.format(formatter);

        return formattedOrderTime;
    }

    private String makeOrderTitle(PostOrderRequestDto postOrderRequestDto) {
        OrderSpecific firstAtOrderSpecific = postOrderRequestDto.getOrderList().get(0);

        String firstAtOrderList = firstAtOrderSpecific.getItemName();
        int firstAtOrderListCount = firstAtOrderSpecific.getItemCount();    // 어차피 String이랑 concat 될 때 int로 unboxing된다고 함. 미리 unboxing.
        int orderListCount = postOrderRequestDto.getOrderList().size() - 1;    // -1이 붙어서 어차피 unboxing 거치니까 int로
        String orderTitle = firstAtOrderList + " " + firstAtOrderListCount + "개 외 " + orderListCount + "건";

        return orderTitle;
    }

    private int[] calcTotalPriceAndSavedMoney(List<OrderSpecific> orderList) {  // 할인 금액의 합계, 아낀 금액 계산
        int totalPrice = 0; // 할인 금액의 합계(상품들 원래가격의 합 구한 후 아낀 금액 차감)
        int savedMoney = 0; // 아낀 금액(totalItemPrice - totalPrice)
        int[] totalPriceAndSavedMoney = new int[2]; // 0: 할인 금액의 합계, 1: 아낀 금액
        for (int i = 0; i < orderList.size(); i++) {
            OrderSpecific orderSpecific = orderList.get(i);
            totalPrice += orderSpecific.getSalePrice() * orderSpecific.getItemCount();
            savedMoney += orderSpecific.getItemPrice() * orderSpecific.getItemCount();
        }
        savedMoney -= totalPrice;
        totalPriceAndSavedMoney[0] = totalPrice;
        totalPriceAndSavedMoney[1] = savedMoney;

        return totalPriceAndSavedMoney;
    }

    private OrderDto postOrderDTOtoOrderDTO(User userEntity, Long storeId, PostOrderRequestDto postOrderRequestDto, String formattedNowTime) {
        Store store = storeRepository.findById(storeId).get();
        String orderTitle = makeOrderTitle(postOrderRequestDto);
        int[] totalPriceAndSavedMoney = calcTotalPriceAndSavedMoney(postOrderRequestDto.getOrderList());
        Integer totalPrice = totalPriceAndSavedMoney[0];
        Integer savedMoney = totalPriceAndSavedMoney[1];

        OrderDto orderDto = new OrderDto();
        orderDto.setStoreId(storeId);
        orderDto.setUserId(userEntity.getUserId());
        orderDto.setOrderStatus(OrderStatus.NEW);
        orderDto.setUserName(userEntity.getUserName());
        orderDto.setPhoneNumber(userEntity.getPhoneNumber());
        orderDto.setOrderTitle(orderTitle);    // 크로플 3개 외 4건
        orderDto.setOrderTime(formattedNowTime);
        orderDto.setVisitTime(postOrderRequestDto.getVisitTime());
        orderDto.setStoreName(store.getStoreName());
        orderDto.setStoreAddress(store.getStoreAddress());
        orderDto.setStoreContact(store.getStoreContact());
        orderDto.setCategory(store.getCategory());
        orderDto.setOrderList(postOrderRequestDto.getOrderList());
        orderDto.setTotalPrice(totalPrice);
        orderDto.setSavedMoney(savedMoney);

        return orderDto;
    }

    public void saveFirstOrderData(String registerTime, String orderTime) {
        FirstOrderData firstOrderData = FirstOrderData.builder(orderTime)
                .registerTime(registerTime)
                .build();

        firstOrderDataRepository.save(firstOrderData);
    }

    public void sendMessage(Long storeId, String title, String message) {
        Store storeEntity = storeRepository.findById(storeId).get();
        List<String> deviceTokens = new ArrayList<>(storeEntity.getDeviceTokens());
        for (int i = 0; i < deviceTokens.size(); i++) {
            String deviceToken = deviceTokens.get(i);
            FCMAlertDto fcmAlertDto = new FCMAlertDto(deviceToken, title, message);
            String result = fcmService.sendMessage(fcmAlertDto);
            System.out.println("사장님 앱에 보낸 알림, 디바이스 토큰 " + deviceToken + "'s result : " + result);
        }
    }

}
