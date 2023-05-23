package dto.auth.customer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserResponseDto {

    @Schema(description = "처리 결과 메세지를 담은 DTO")
    @AllArgsConstructor
    @Getter
    @Setter
    public static class MessageDto {
        @Schema(description = "처리 결과 메세지", example = "Refresh token validation failed. Login required(리프레시 토큰 유효성 검증 실패, 재로그인 요청)," +
                " Sign-out successful(로그아웃 성공)," + " Access token invalid(액세스 토큰 유효성 검증 실패)," + " Access token expired(액세스 토큰 만료)," + " etc...")
        private String message;
    }

    @Schema(description = "회원탈퇴 결과 메세지를 담은 DTO")
    @AllArgsConstructor
    @Getter
    @Setter
    public static class DeleteUserDto {
        @Schema(description = "처리 결과 메세지", example = " Delete user successful")
        private String message;
        @Schema(description = "애플 회원탈퇴에 필요한 client secret(jwt), 다른 플랫폼은 null", example = "jwt token(or null with platform except for apple)")
        private String clientSecret;
    }

}
