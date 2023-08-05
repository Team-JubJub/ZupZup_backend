package dto.info.customer.response;

import dto.auth.customer.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchInfoResponseDto {

    private UserDto data;
    private String message;

}