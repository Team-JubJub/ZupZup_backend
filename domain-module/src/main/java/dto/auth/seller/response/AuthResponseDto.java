package dto.auth.seller.response;

import lombok.Getter;

public class AuthResponseDto {

    @Getter
    public static class SignInResponseDto {
        private String storeId;
    }

}
