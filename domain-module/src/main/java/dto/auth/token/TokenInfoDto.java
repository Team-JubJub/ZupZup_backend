package dto.auth.token;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class TokenInfoDto {

    private String result;
    private String message;
    private String accessToken;
    private String refreshToken;

    public TokenInfoDto(String result, String message, String accessToken, String refreshToken) {
        this.result = result;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
