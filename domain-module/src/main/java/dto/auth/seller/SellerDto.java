package dto.auth.seller;

import domain.auth.Role;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SellerDto {

    private Long sellerId;
    private String loginId;
    private String loginPwd;

    private Role role;
}
