package dto.auth.customer;


import domain.auth.User.Provider;
import domain.auth.User.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long userId;
    private String providerUserId;  // ex) NAVER_userID

    private String nickName;
    private String gender;
    private String phoneNumber;

    private Role role;
    private Provider provider;

    private Boolean essentialTerms;
    private Boolean optionalTerm1;

}
