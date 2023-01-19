package zupzup.back_end.reservation.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@Log
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrder() {  // service layer에서는 entity 그대로 return, controller에서 dto로 변환
        List<Order> allOrderList = orderRepository.findAll();
        return allOrderList;
    }

    @Override
    public Order getOrderById(Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent()) {
            Order orderEntity = order.get();
            return orderEntity;
        } else {
            return null;    // 예외처리 나중에 해놓을 것.
        }
    }
}
