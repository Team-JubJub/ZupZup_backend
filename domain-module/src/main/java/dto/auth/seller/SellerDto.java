package dto.auth.seller;

import domain.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class SellerDto {

    private Long sellerId;
    private String loginId;
    private String loginPwd;

    private String name;
    private String phoneNumber;
    private String email;

    private Role role;
}
