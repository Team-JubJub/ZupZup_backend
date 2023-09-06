package dto.auth.seller.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SellerTestSignUpDto {

    private String loginId;
    private String loginPwd;

    private String name;
    private String phoneNumber;
    private String email;

}