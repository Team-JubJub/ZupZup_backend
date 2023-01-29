package zupzup.back_end.reservation.service;

import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.reservation.domain.type.OrderSpecific;
import zupzup.back_end.reservation.domain.type.OrderStatus;
import zupzup.back_end.reservation.exception.OrderNotFoundException;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.dto.OrderRequestDto;
import zupzup.back_end.reservation.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
@Transactional
public class OrderService {

    @Autowired
    ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // <-------------------- GET part -------------------->
    public List<OrderRequestDto.GetOrderDto> getAllOrder(Long storeId) {
        List<Order> allOrderListEntity = orderRepository.findByStore_StoreId(storeId);
        List<OrderRequestDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, OrderRequestDto.GetOrderDto.class))
                .collect(Collectors.toList());

        return allOrderListDto;
    }

    public OrderRequestDto.GetOrderSpecificDto getOrderById(Long storeId, Long orderId) {
        Order orderEntity = orderRepository.findById(orderId).get();
        isOrderInStore(storeId, orderEntity);
        OrderRequestDto.GetOrderSpecificDto getOrderSpecificDto = modelMapper.map(orderEntity, OrderRequestDto.GetOrderSpecificDto.class);

        return getOrderSpecificDto;
    }

    // <-------------------- PATCH part -------------------->
    public OrderRequestDto.GetOrderSpecificDto patchOrderById(Long storeId, Long orderId, OrderRequestDto.PatchOrderDto patchOrderDto) {
        Order orderEntity = orderRepository.findById(orderId).get();
        isOrderInStore(storeId, orderEntity);

        /*
            PatchOrderDto로 받아온 사장님이 입력한 예약 확정 내역(아이템 개수 등)과 orderEntity 비교해서
            Entity의 내용 수정, 수정된 내용 dto로 반환
         */
        OrderRequestDto.GetOrderSpecificDto orderEntityDto = modelMapper.map(orderEntity, OrderRequestDto.GetOrderSpecificDto.class);
        List<OrderSpecific> requestedOrderSpecific = patchOrderDto.getOrderList();
        for(int i=0; i < requestedOrderSpecific.size(); i++) {  // 사장님이 컨펌한 것과 원래 주문 요청에서의 개수가 하나라도 다르면
            if(orderEntityDto.getOrderList().get(i).getItemCount() != requestedOrderSpecific.get(i).getItemCount()) {
                orderEntityDto.setOrderList(requestedOrderSpecific);    // orderList 변경 및
                orderEntityDto.setOrderStatus(OrderStatus.PARTIAL); // 주문상태 부분확정으로

            }
        }   // flag, 리턴 값 정의 해결하기

        OrderRequestDto.GetOrderSpecificDto patchedOrderSpecificDto = modelMapper.map(orderEntity, OrderRequestDto.GetOrderSpecificDto.class);

        return patchedOrderSpecificDto;
    }


    // <-------------------- Common methods part -------------------->
    private void isOrderInStore(Long storeId, Order orderEntity) {  // 해당 주문이 가게에 존재하는 주문이 아닐 경우 보여주면 안되므로 예외처리
        if(orderEntity.getStore().getStoreId() != storeId){ // 지금의 로직은 'order의 store id'가 path의 store id와 다른 경우.
            throw new OrderNotFoundException();             // 내가 원했던 로직은 '해당 store에' 그 order가 없을 경우 에러 핸들링이므로 로직 수정 해야함.
        }
    }
}
