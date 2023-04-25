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
        @Schema(description = "처리 결과 메세지", example = "Refresh token validation failed. Login required, Token invalid, Sign-out successful, Token expired, etc...")
        private String message;
    }

}
