package dto.info.customer.response;

import dto.auth.customer.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchInfoResponseDto {

    @Schema(description = "변경된 유저의 정보", implementation = UserDto.class)
    private UserDto data;

    @Schema(description = "처리 결과 메세지")
    private String message;

}
