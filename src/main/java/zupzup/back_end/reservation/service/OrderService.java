package zupzup.back_end.reservation.service;

import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.reservation.Exception.OrderNotFoundException;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.dto.OrderDto;
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
    public List<OrderDto.GetOrderDto> getAllOrder(Long storeId) {
        List<Order> allOrderListEntity = orderRepository.findByStore_StoreId(storeId);
        List<OrderDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, OrderDto.GetOrderDto.class))
                .collect(Collectors.toList());

        return allOrderListDto;
    }

    public OrderDto.GetOrderSpecificDto getOrderById(Long storeId, Long orderId) {
        Order orderEntity = orderRepository.findById(orderId).get();
        isOrderInStore(storeId, orderEntity);
        OrderDto.GetOrderSpecificDto getOrderSpecificDto = modelMapper.map(orderEntity, OrderDto.GetOrderSpecificDto.class);

        return getOrderSpecificDto;
    }

    // <-------------------- PATCH part -------------------->
    public OrderDto.GetOrderSpecificDto patchOrderById(Long storeId, Long orderId, OrderDto.PatchOrderDto patchOrderDto) {
        Order orderEntity = orderRepository.findById(orderId).get();
        isOrderInStore(storeId, orderEntity);
        /*
            PatchOrderDto로 받아온 사장님이 입력한 예약 확정 내역(아이템 개수 등)과 orderEntity 비교해서
            Entity의 내용 수정, 수정된 내용 dto로 반환
         */

        OrderDto.GetOrderSpecificDto patchedOrderSpecificDto = modelMapper.map(orderEntity, OrderDto.GetOrderSpecificDto.class);

        return patchedOrderSpecificDto;
    }
    // <-------------------- Common methods part -------------------->

    private void isOrderInStore(Long storeId, Order orderEntity) {
        if(orderEntity.getStore().getStoreId() != storeId){
            throw new OrderNotFoundException();
        }
    }
}
