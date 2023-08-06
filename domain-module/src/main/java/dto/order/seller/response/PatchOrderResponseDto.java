package dto.order.seller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchOrderResponseDto { // PATCH 시 return 할 DTO(신규 주문 확정, 확정 주문 취소 시에만 사용)

    private GetOrderDetailsDto data;
    private String message;

}