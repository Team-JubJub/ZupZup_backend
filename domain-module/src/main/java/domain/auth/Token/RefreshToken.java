package domain.auth.Token;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


import java.time.LocalDateTime;

@RedisHash(value = "refreshToken", timeToLive = 100*60*60*24*14)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String id;
    @Indexed
    private String providerUserId;  // User id(우선은 조인시키지 말고)
    private String accessToken; // 로그아웃 된 유저의 경우 access token을 저장해놓음.
    private String refreshToken;
    private LocalDateTime expiredAt;

    private RefreshToken(final String providerUserId, final String refreshToken, final LocalDateTime expiredAt) {
        this.providerUserId = providerUserId;
        this.refreshToken = refreshToken;
        this.expiredAt = expiredAt;
    }

    public Boolean isExpiredAt(final LocalDateTime now) {
        return now.isAfter(expiredAt);
    }

    public void changeToken(String token) {
        this.refreshToken = token;
    }

}
