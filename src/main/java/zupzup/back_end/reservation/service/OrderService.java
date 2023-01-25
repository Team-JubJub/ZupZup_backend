package zupzup.back_end.reservation.service;

import zupzup.back_end.reservation.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<OrderDto.GetOrderDto> getAllOrder() throws Exception;
    OrderDto.GetOrderSpecificDto getOrderById(Long orderId) throws Exception;
    OrderDto.GetOrderSpecificDto patchOrderById(OrderDto.PatchOrderDto patchOrderDto, Long orderId) throws Exception;
}
