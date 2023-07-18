package dto.auth.seller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class SellerResponseDto {

    @Schema(description = "사장님 앱 로그인의 response")
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class TestSignInResponseDto {
        @Schema(description = "사장님 앱 로그인 결과의 메세지", example = "Login fails, Login success")
        private String message;
        @Schema(description = "가게의 키값")
        private Long storeId;
    }

}
