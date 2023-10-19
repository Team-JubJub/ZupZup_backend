package dto.auth.customer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "애플의 refreshToken을 담은 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppleRefreshTokenDto {

    @Schema(description = "키체인에 저장 될 애플의 refresh token")
    private String appleRefreshToken;

}
