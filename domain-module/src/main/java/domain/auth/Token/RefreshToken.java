package domain.auth.Token;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;


import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@RedisHash(value = "refreshTokens")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private String id;
//    @Indexed
    private String providerUserId;  // User id(우선은 조인시키지 말고)
//    @Indexed
    private String accessToken; // 로그아웃 된 유저의 경우 access token을 저장해놓음.
//    @Indexed
    private String refreshToken;
    @TimeToLive(unit = TimeUnit.MILLISECONDS)
    private Integer expiration;

    public RefreshToken(final String providerUserId, final String refreshToken, final Integer expiration) {
        this.providerUserId = providerUserId;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

//    public Boolean isExpiredAt(final LocalDateTime now) {
//        return now.isAfter(LocalDateTime().now().expiration);
//    }

    public void changeToken(String token) {
        this.refreshToken = token;
    }

}
