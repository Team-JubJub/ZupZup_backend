package domain.auth.Token.response;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ValidRefreshTokenResponse {

    private String providerUserId;
    private int status;
    private String accessToken;

    public ValidRefreshTokenResponse(String providerUserId, int status, String accessToken) {
        this.providerUserId = providerUserId;
        this.status = status;
        this.accessToken = accessToken;
    }

}
