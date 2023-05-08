package dto.auth.seller.request;

import lombok.Getter;

public class AuthRequestDto {

    @Getter
    public static class SellerSignInDto {
        private String loginId;
        private String loginPwd;
    }

    @Getter
    public static class SellerTestSingUpDto {
        private String loginId;
        private String loginPwd;
    }

}
