package dto.info.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchPhoneNumberDto {

    @Schema(description = "010-xxxx-xxxx 포맷의 인증 완료된 전화번호", example = "010-1234-5678")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "Phone number pattern should be like 010-xxxx-xxxx")
    String phoneNumber;

}
