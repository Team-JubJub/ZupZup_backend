package dto.order.customer.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostOrderResponseDto {

    private OrderResponseDto.GetOrderDetailsDto data;
    private String href;
    private String message;

}
