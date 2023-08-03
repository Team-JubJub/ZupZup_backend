package dto.auth.seller.test;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "사장님 앱 로그인의 test response")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TestSignInResponseDto {

    @Schema(description = "사장님 앱 로그인 결과의 메세지", example = "Login fails, Login success")
    private String message;
    @Schema(description = "가게의 키값")
    private Long storeId;

}
