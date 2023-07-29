package dto.auth.customer;


import domain.auth.User.Provider;
import domain.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long userId;
    private String providerUserId;  // ex) NAVER_userID
    private String userName;

    private String nickName;
    private String gender;
    private String phoneNumber;

    private Role role;
    private Provider provider;

    private Boolean essentialTerms;
    private Boolean optionalTerm1;

}
