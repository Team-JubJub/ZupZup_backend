package dto.auth.token;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RefreshResultDto {

    private String result;
    private String message;
    private String providerUserId;
    private String accessToken;

    public RefreshResultDto(String result, String message, String providerUserId, String accessToken) {
        this.result = result;
        this.message = message;
        this.providerUserId = providerUserId;
        this.accessToken = accessToken;
    }

}
