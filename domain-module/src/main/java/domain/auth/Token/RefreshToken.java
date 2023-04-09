package domain.auth.Token;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refreshTokenId")
    private Long refreshTokenId;

    private String providerUserId;  // User id(우선은 조인시키지 말고)
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
