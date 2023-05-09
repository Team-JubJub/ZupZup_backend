package dto.auth.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class AuthRequestDto {

    @Schema(description = "사장님 앱 로그인에 사용되는 DTO")
    @Getter
    public static class SellerSignInDto {
        @Schema(description = "사장님 앱 로그인에 사용되는 ID", example = "test123")
        private String loginId;
        @Schema(description = "사장님 앱 로그인에 사용되는 비밀번호", example = "test123")
        private String loginPwd;
    }

    @Getter
    public static class SellerTestSingUpDto {
        private String loginId;
        private String loginPwd;
    }

}
