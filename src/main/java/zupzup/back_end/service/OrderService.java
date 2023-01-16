package zupzup.back_end.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zupzup.back_end.dto.OrderDto;
import zupzup.back_end.dto.request.OrderRequestDto;
import zupzup.back_end.repository.OrderRepository;

import java.util.List;

@Service
@Log
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public void saveOrder(OrderDto orderDto) {

        List<OrderRequestDto> orderList = orderDto.getOrderList();
        for(OrderRequestDto requestDto : orderList) {

            //if()
        }
    }
}
