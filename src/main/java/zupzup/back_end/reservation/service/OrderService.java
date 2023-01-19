package zupzup.back_end.reservation.service;

import zupzup.back_end.reservation.domain.Order;

import java.util.List;

public interface OrderService {
    public List<Order> getAllOrder() throws Exception;
    public Order getOrderById(Long reservId) throws Exception;
}
