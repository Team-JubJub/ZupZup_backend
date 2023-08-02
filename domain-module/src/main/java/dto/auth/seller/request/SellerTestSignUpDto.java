package dto.auth.seller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SellerTestSignUpDto {

    private String loginId;
    private String loginPwd;

}