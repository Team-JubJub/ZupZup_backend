package domain.auth.User;

import domain.auth.Role;
import dto.info.customer.request.PatchNickNameDto;
import dto.info.customer.request.PatchOptionalTermDto;
import dto.info.customer.request.PatchPhoneNumberDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "UserBuilder")
@Table(name = "users")   // 나중에 customer, seller 구분해서 만들지 고민할 것
public class User {

    @Id
    @Column(name = "userId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;    // auto increment id
    @Enumerated(EnumType.STRING) private Provider provider;  // 가입 시 사용한 플랫폼
    @Column(nullable = false) private String providerUserId;  // ex) KAKAO_user123
    @Column(nullable = false) private String userName;    // 유저의 실명
    @Column(nullable = false) private String nickName;  // 닉네임
    @Column(nullable = false) private String gender;    // 성별
    @Column(nullable = false) private String phoneNumber;   // 유저의 연락처

    @Column(nullable = true)
    @ElementCollection
    @CollectionTable(name = "starredStores", joinColumns = @JoinColumn(name="userId", referencedColumnName="userId"))
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Long> starredStores;    // 찜한 가게 아이디들

    @Column(nullable = false) private Boolean essentialTerms;   // 필수 약관 동의 여부
    @Column(nullable = false) private Boolean optionalTerm1;    // 선택 약관1 동의 여부
    @Column(nullable = false) private String registerTime;  // 가입 시간(LocalDateTime, 현재는 KST 기준)
    @Column(nullable = false) private int orderCount;   // 주문 횟수(바로바로 횟수 계산이 가능하게끔 primitive type으로)

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;  // 유저 권한

    public static UserBuilder builder(String providerUserId) {  // 현재 필수 파라미터는 임시
        if(providerUserId.equals(null)) {
            throw new IllegalArgumentException("필수 파라미터(providerUserId) 누락");
        }
        return UserBuilder().providerUserId(providerUserId);
    }

    public void updatePhoneNumber(PatchPhoneNumberDto patchPhoneNumberDto) {
        this.phoneNumber = patchPhoneNumberDto.getPhoneNumber();
    }

    public void updateNickName(PatchNickNameDto patchNickNameDto) {
        this.nickName = patchNickNameDto.getNickName();
    }

    public void updateOptionalTerm1(PatchOptionalTermDto patchOptionalTermDto) {
        this.optionalTerm1 = patchOptionalTermDto.getOptionalTerm1();
    }

}
