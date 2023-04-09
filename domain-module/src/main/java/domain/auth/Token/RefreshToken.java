package domain.auth.Token;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private RefreshToken(String providerUserId, String refreshToken) {
        this.providerUserId = providerUserId;
        this.refreshToken = refreshToken;
    }
    public static RefreshToken createToken(String providerUserId, String refreshToken){
        return new RefreshToken(providerUserId, refreshToken);
    }

    public void changeToken(String token) {
        this.refreshToken = token;
    }

}
