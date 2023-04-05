package domain.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "UserBuilder")
@Table(name = "users")   // 나중에 customer, seller 구분해서 만들지 고민할 것
public class User {

    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String providedId;
    private String refreshToken;

    @Column(nullable = false) private String nickName;
    @Column(nullable = false) private String gender;
    @Column(nullable = false) private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false) private Boolean essentialTerms;
    @Column(nullable = false) private Boolean optionalTerm1;

    public static UserBuilder builder(Provider provider) {  // 현재 필수 파라미터는 임시
        if(provider == null) {
            throw new IllegalArgumentException("필수 파라미터(제공자) 누락");
        }
        return UserBuilder().provider(provider);
    }

}
