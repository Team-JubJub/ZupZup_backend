package dto.auth.customer;

import Provider;
import Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long userId;
    private String providerUserId;  // ex) NAVER_userID
    private String refreshToken;

    private String nickName;
    private String gender;
    private String phoneNumber;

    private Role role;
    private Provider provider;

    private Boolean essentialTerms;
    private Boolean optionalTerm1;

}
