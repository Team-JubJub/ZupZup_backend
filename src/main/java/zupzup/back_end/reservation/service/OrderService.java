package zupzup.back_end.reservation.service;

import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.domain.OrderRequest;
import zupzup.back_end.reservation.dto.OrderDto;

import java.util.List;

public interface OrderService {
    public List<Order> getAllOrder() throws Exception;
    List<OrderRequest> getOrderById(Long reservId) throws Exception;
}
