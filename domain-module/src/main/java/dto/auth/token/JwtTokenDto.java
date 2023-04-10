package dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class JwtTokenDto {

    private String grantType;
    private String accessToken;
    private String refreshToken;

}
