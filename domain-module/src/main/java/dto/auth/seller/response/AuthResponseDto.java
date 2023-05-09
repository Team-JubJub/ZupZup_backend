package dto.auth.seller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class AuthResponseDto {

    @Schema(description = "사장님 앱 로그인의 response")
    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class SignInResponseDto {
        @Schema(description = "사장님 앱 로그인 결과의 메세지", example = "Login fails, Login success")
        private String message;
        @Schema(description = "가게의 키값", example = "-1(실패시), 0(테스트 용), 1~(성공 시 리턴하는 가게의 키값)")
        private Long fireBaseStoreId;
    }

}
