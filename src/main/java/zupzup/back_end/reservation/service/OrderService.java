package zupzup.back_end.reservation.service;

import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import zupzup.back_end.reservation.domain.type.OrderSpecific;
import zupzup.back_end.reservation.domain.type.OrderStatus;
import zupzup.back_end.reservation.dto.OrderDto;
import zupzup.back_end.reservation.dto.OrderResponseDto;
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
    public List<OrderResponseDto.GetOrderDto> getAllOrder(Long storeId) {
        List<Order> allOrderListEntity = orderRepository.findByStore_StoreId(storeId);
        List<OrderResponseDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, OrderResponseDto.GetOrderDto.class))
                .collect(Collectors.toList());

        return allOrderListDto;
    }

    public OrderResponseDto.GetOrderSpecificDto getOrderById(Long storeId, Long orderId) {
        Order orderEntity = orderRepository.findById(orderId).get();
        isOrderInStore(storeId, orderEntity);
        OrderResponseDto.GetOrderSpecificDto getOrderSpecificDto = modelMapper.map(orderEntity, OrderResponseDto.GetOrderSpecificDto.class);

        return getOrderSpecificDto;
    }

    // <-------------------- PATCH part -------------------->
    public String patchOrderById(Long storeId, Long orderId, OrderRequestDto.PatchOrderDto patchOrderDto) {
        Order orderEntity = orderRepository.findById(orderId).get();
        isOrderInStore(storeId, orderEntity);

        List<OrderSpecific> requestedOrderSpecific = patchOrderDto.getOrderList();
        int totalItemCount = 0; // 주문 취소 여부를 확인 위한 변수. 0일 경우(모든 상품 재고가 없을 경우) 부분확정이 아닌 주문 취소.

        for(int i=0; i < requestedOrderSpecific.size(); i++) {  // 사장님이 컨펌한 것과 원래 주문 요청에서의 개수가 하나라도 다르면
            int requestedItemCount = requestedOrderSpecific.get(i).getItemCount();
            totalItemCount = totalItemCount + requestedItemCount;
            if(orderEntity.getOrderList().get(i).getItemCount() != requestedItemCount) { // 지금은 같은 상품끼리 같은 인덱스일 거라 간주하고 하는데, item id나 이름으로 조회 하는 방법으로 바꿀 것.
                orderEntity.getOrderList().get(i).setItemCount(requestedItemCount);
                orderEntity.setOrderStatus(OrderStatus.PARTIAL); // 주문상태 부분확정으로
            }

            if(i == requestedOrderSpecific.size()-1 && totalItemCount == 0){    // 주문 취소
                orderEntity.setOrderStatus(OrderStatus.CANCEL);
            }
        }
        orderRepository.save(orderEntity);    // Item repository의 개수 변경도 구현할 것.
        if(orderEntity.getOrderStatus() == OrderStatus.CANCEL){
            return "주문이 취소되었습니다.";
        }
        else if(orderEntity.getOrderStatus() == OrderStatus.PARTIAL){
            return "주문이 부분확정되었습니다.";
        }

        return "주문이 확정되었습니다.";
    }


    // <-------------------- Common methods part -------------------->
    private void isOrderInStore(Long storeId, Order orderEntity) {  // 해당 주문이 가게에 존재하는 주문이 아닐 경우 보여주면 안되므로 예외처리
        if(orderEntity.getStore().getStoreId() != storeId){ // 지금의 로직은 'order의 store id'가 path의 store id와 다른 경우.
            throw new OrderNotFoundException();             // 내가 원했던 로직은 '해당 store에' 그 order가 없을 경우 에러 핸들링이므로 로직 수정 해야함.
        }
    }
}
