package dto.auth.token;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ValidRefreshTokenResponseDto {

    private String providerUserId;
    private int status;
    private String accessToken;

    public ValidRefreshTokenResponseDto(String providerUserId, int status, String accessToken) {
        this.providerUserId = providerUserId;
        this.status = status;
        this.accessToken = accessToken;
    }

}
