package dto.store.customer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "가게의 찜/알림 설정 시 반환되는 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StarAlertResponseDto {

    boolean result;

}
