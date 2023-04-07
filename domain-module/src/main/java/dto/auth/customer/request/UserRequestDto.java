package dto.auth.customer.request;

import lombok.Getter;
import lombok.Setter;

public class UserRequestDto {

    @Getter
    @Setter
    public class UserOAuthLoginDto {
        private String userUniqueId;    // 소셜 플랫폼에서 리턴해주는 user의 unique ID
    }

}
