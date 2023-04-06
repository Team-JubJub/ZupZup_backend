package dto.auth.customer;

import domain.auth.Provider;
import domain.auth.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;
    private String providedId;
    private String refreshToken;

    private String nickName;
    private String gender;
    private String phoneNumber;

    private Role role;
    private Provider provider;

    private Boolean essentialTerms;
    private Boolean optionalTerm1;

}
