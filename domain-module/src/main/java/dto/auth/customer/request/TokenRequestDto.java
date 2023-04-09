package dto.auth.customer.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class TokenRequestDto {

    private String access_token;
    private String refresh_token;
    private String userUniqueId;

}
