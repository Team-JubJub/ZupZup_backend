package domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetSocialOAuthResult { // 소셜로그인 공통 result로 사용할 클래스

    String email;
    String password;

}
