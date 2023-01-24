package zupzup.back_end.reservation.service;

import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.dto.OrderDto;
import zupzup.back_end.reservation.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    ModelMapper modelMapper;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // <-------------------- GET part -------------------->
    @Override
    public List<OrderDto.GetOrderDto> getAllOrder() {
        List<Order> allOrderListEntity = orderRepository.findAll();
        List<OrderDto.GetOrderDto> allOrderListDto = allOrderListEntity.stream()   // Entity -> Dto
                .map(m -> modelMapper.map(m, OrderDto.GetOrderDto.class))
                .collect(Collectors.toList());

        return allOrderListDto;
    }

    @Override
    public OrderDto.GetOrderSpecificDto getOrderById(Long orderId) {
        Order orderEntity = orderRepository.findById(orderId).get();
        OrderDto.GetOrderSpecificDto getOrderSpecificDto = modelMapper.map(orderEntity, OrderDto.GetOrderSpecificDto.class);

        return getOrderSpecificDto;
    }
}
