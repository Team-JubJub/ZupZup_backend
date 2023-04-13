package dto.auth.customer.request;

import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class UserSignUpDto {
        private String userUniqueId;
        private String userName;
        private String nickName;
        private String gender;
        private String phoneNumber;
        private Boolean essentialTerms;
        private Boolean optionalTerm1;
    }

    @Getter
    public static class UserSignInDto {
        private String userUniqueId;    // 소셜 플랫폼에서 제공한 user unique ID
        private String providerAccessToken; // 소셜플랫폼에서 받은 access token
    }

}
