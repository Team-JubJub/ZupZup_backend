package dto.auth.seller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class SellerResponseDto {

    @Schema(description = "처리 결과 메세지를 담은 DTO")
    @AllArgsConstructor
    @Getter
    @Setter
    public static class MessageDto {
        @Schema(description = "처리 결과 메세지", example = "Refresh token validation failed. Login required(리프레시 토큰 유효성 검증 실패, 재로그인 요청)," +
                " Sign-out successful(로그아웃 성공)," + " Access token invalid(액세스 토큰 유효성 검증 실패)," + " Access token expired(액세스 토큰 만료)," + " etc...")
        private String message;
    }

    // <------ Test ------>
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
