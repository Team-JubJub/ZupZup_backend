package dto.auth.customer;


import domain.auth.User.Provider;
import domain.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long userId;
    private Provider provider;
    private String providerUserId;  // ex) NAVER_userID
    private String userName;
    private String nickName;
    private String gender;
    private String phoneNumber;

    private List<Long> starredStores;
    private List<Long> alertStores;

    private Boolean essentialTerms;
    private Boolean optionalTerm1;
    private String registerTime;
    private int orderCount;

    private String deviceToken;

    private Role role;

}
