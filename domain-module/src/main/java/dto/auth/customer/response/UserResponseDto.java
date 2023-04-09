package dto.auth.customer.response;

import lombok.Getter;

@Getter
public class UserResponseDto {

    public static class TokenResponseDto {
        private String access_token;
        private String refresh_token;
    }

}
