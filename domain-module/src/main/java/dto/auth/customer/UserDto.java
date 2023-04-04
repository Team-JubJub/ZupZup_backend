package dto.auth.customer;

import domain.auth.Provider;
import domain.auth.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private Long id;

    private String email;
    private String password;
    private Role role;
    private Provider provider;

}
