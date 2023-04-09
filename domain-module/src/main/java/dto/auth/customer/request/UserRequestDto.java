package dto.auth.customer.request;

import lombok.Getter;

public class UserRequestDto {

    @Getter
    public static class UserSignUpDto {
        private String userUniqueId;
        private String nickName;
        private String gender;
        private String phoneNumber;
        private Boolean essentialTerms;
        private Boolean optionalTerm1;
    }

}
