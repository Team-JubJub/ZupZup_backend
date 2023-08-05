package dto.auth.customer.response;

import dto.auth.customer.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchNicknameResponseDto {

    private UserDto data;
    private String message;

}
