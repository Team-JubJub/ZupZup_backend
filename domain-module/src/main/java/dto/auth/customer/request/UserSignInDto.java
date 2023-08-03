package dto.auth.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "재로그인 요청 시 사용되는 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSignInDto {

    @Schema(description = "소셜 플랫폼에서 받아온 유저의 unique ID", example = "im1from2naver3")
    @NotBlank(message = "User unique id cannot be null or empty or space")
    private String userUniqueId;    // 클라이언트에서 제공한 소셜 플랫폼의 user unique ID

}