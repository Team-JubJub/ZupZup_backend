package dto.auth.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetAppleRefreshTokenDto {

    @Schema(description = "애플의 refreshToken을 얻어올 때 사용할 authCode")
    String authCode;

}
