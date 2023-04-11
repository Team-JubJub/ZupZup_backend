package dto.auth.customer.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserResponseDto {

    @AllArgsConstructor
    @Getter
    @Setter
    public static class MessageDto {
        private String message;
    }

}
