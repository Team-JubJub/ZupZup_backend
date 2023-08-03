package dto.auth.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Schema(description = "계정 찾기 시 사용되는 DTO")   // 아직 어떤 식으로 찾을지 몰라서 놔둠
@Getter
public class AccountRecoveryDto {

    @Schema(description = "계정을 찾고자 하는 유저의 전화번호", example = "010-2828-7449")
    @NotBlank(message = "PhoneNumber cannot be null or empty or space")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "Phone number pattern should be like 010-xxxx-xxxx")
    private String phoneNumber;    // 클라이언트에서 제공한 소셜 플랫폼의 user unique ID

}
