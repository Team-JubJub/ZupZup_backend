package dto.auth.customer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "처리 결과 메세지를 담은 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageDto {

    @Schema(description = "처리 결과 메세지", example = "Refresh token validation failed. Login required(리프레시 토큰 유효성 검증 실패, 재로그인 요청)," +
            " Sign-out successful(로그아웃 성공)," + " Access token invalid(액세스 토큰 유효성 검증 실패)," + " Access token expired(액세스 토큰 만료)," + " etc...")
    private String message;

}
