package zupzup.back_end.reservation.dto;

import lombok.Getter;
import lombok.Setter;
import zupzup.back_end.reservation.domain.Order;
import zupzup.back_end.reservation.domain.type.OrderSpecific;

import java.util.List;

public class OrderRequestDto {
    // <-------------------- PATCH part -------------------->
    @Getter
    @Setter
    public static class PatchOrderDto {
        private List<OrderSpecific> orderList; // 주문 품목 이름, 가격, 개수
    }

    public void toServiceDto(String mode) {
        if(mode == "C") {
            OrderServiceDto.PostOrderDto orderServiceDto = new OrderServiceDto.PostOrderDto();
        }
        else if(mode == "R") {
            OrderServiceDto.GetOrderDto orderServiceDto = new OrderServiceDto.GetOrderDto();
        }
        else if(mode == "U") {
            OrderServiceDto.PatchOrderDto orderServiceDto = new OrderServiceDto.PatchOrderDto();
        }
        else if(mode == "D") {
            OrderServiceDto.DeleteOrderDto orderServiceDto = new OrderServiceDto.DeleteOrderDto();
        }
    }
}
