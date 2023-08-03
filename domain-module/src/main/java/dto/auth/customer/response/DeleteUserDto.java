package dto.auth.customer.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "회원탈퇴 결과 메세지를 담은 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DeleteUserDto {

    @Schema(description = "처리 결과 메세지", example = " Delete user successful")
    private String message;
    @Schema(description = "애플 회원탈퇴에 필요한 client secret(jwt), 다른 플랫폼은 null", example = "jwt token(or null with platform except for apple)")
    private String clientSecret;

}