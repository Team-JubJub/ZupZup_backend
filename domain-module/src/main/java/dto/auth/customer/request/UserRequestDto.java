package dto.auth.customer.request;

import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class UserCheckDto {
        private String access_token;
        private String refresh_token;
        private String userUniqueId;
    }

    @Getter
    public static class UserSignUpDto {
        private String nickName;
        private String gender;
        private String phoneNumber;
        private Boolean essentialTerms;
        private Boolean optionalTerm1;
    }

}
