package domain.auth.Seller;

import domain.auth.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Setter @Getter
@Table(name = "seller")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "SellerBuilder")
public class Seller {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerId;

    @Column(nullable = false)
    private String loginId;
    @Column(nullable = false)
    private String loginPwd;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;  // 사장님의 앱 권한 -> 우선 ROLE_SELLER로 통일

    public SellerBuilder builder(String loginId) {
        if (loginId.equals(null)) {
            throw new IllegalArgumentException("필수 파라미터(loginId) 누락");
        }
        return SellerBuilder().sellerId(sellerId);
    }
    
}
