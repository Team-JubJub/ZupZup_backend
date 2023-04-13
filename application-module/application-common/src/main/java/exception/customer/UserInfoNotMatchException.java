package exception.customer;

import org.springframework.http.HttpStatus;

public class UserInfoNotMatchException extends AuthRuntimeException {

    private static final String MESSAGE = "요청한 회원의 정보가 일치하지 않습니다.";

    public UserInfoNotMatchException() {
        super(MESSAGE, HttpStatus.UNAUTHORIZED);
    }

}
