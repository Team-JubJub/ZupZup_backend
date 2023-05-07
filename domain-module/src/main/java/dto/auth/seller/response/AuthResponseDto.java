package dto.auth.seller.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class AuthResponseDto {

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class SignInResponseDto {
        private String storeId;
    }

}
