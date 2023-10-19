package dto.auth.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DeleteAppleUserDto {

    @Schema(description = "애플 회원탈퇴에 필요한 authCode(apple 유저의 회원탈퇴가 아닐 시에는 빈 문자열을 요구)", allowableValues = {"", "authCode"})
    String authCode;

}
