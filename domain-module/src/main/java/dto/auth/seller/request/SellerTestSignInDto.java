package dto.auth.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "사장님 앱 로그인에 사용되는 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SellerTestSignInDto {
    @Schema(description = "사장님 앱 로그인에 사용되는 ID", example = "test123")
    private String loginId;
    @Schema(description = "사장님 앱 로그인에 사용되는 비밀번호", example = "test1234")
    private String loginPwd;
}