package dto.auth.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SellerRequestDto {

    // < ------ Test ------ >
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public class SellerTestSignUpDto {
        private String loginId;
        private String loginPwd;
    }

}
