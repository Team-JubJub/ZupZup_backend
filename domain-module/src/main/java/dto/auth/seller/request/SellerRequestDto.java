package dto.auth.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SellerRequestDto {

    // < ------ Test ------ >
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

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public class SellerTestSignUpDto {
        private String loginId;
        private String loginPwd;
    }

}
