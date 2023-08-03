package dto.auth.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "재로그인 요청 시 사용되는 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SellerSignInDto {

    @Schema(description = "사장님의 로그인 ID", example = "test123")
    @NotBlank(message = "ID cannot be null or empty or space")
    private String loginId;    // 사장님의 로그인 id
    @Schema(description = "사장님의 로그인 PW", example = "test1234")
    @NotBlank(message = "PW cannot be null or empty or space")
    private String loginPwd;    // 사장님의 로그인 pwd

}
