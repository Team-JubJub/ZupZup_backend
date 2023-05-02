package dto.auth.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

public class UserRequestDto {

    @Schema(description = "회원가입 요청 시 사용되는 DTO")
    @Getter
    public static class UserSignUpDto {
        @Schema(description = "소셜 플랫폼에서 받아온 유저의 unique ID", example = "im1from2naver3")
        private String userUniqueId;
        @Schema(description = "소셜 플랫폼에서 받아온 유저의 실명", example = "위대한")
        private String userName;
        @Schema(description = "줍줍에 회원가입 시 입력한 유저의 닉네임(특수문자, 공백 불가, 12bytes(한:2bytes, 영어&숫자: 1byte) 제한)", example = "S2줍줍화이팅")
        @Size(min = 4, max = 12, message = "Size must be in range 4bytes to 12bytes")
        private String nickName;
        @Schema(description = "성별", allowableValues = {"male", "female"})
        private String gender;
        @Schema(description = "010-xxxx-xxxx 포맷의 인증 완료된 전화번호", example = "010-1234-5678")
        private String phoneNumber;
        @Schema(description = "필수 약관들의 동의 여부", allowableValues = {"true", "false"})
        private Boolean essentialTerms;
        @Schema(description = "선택 약관 1의 동의 여부", allowableValues = {"true", "false"})
        private Boolean optionalTerm1;
    }

    @Schema(description = "재로그인 요청 시 사용되는 DTO")
    @Getter
    public static class UserSignInDto {
        @Schema(description = "소셜 플랫폼에서 받아온 유저의 unique ID", example = "im1from2naver3")
        private String userUniqueId;    // 클라이언트에서 제공한 소셜 플랫폼의 user unique ID
    }

    @Schema(description = "계정 찾기 시 사용되는 DTO")
    @Getter
    public static class AccountRecoveryDto {
        @Schema(description = "계정을 찾고자 하는 유저의 전화번호", example = "010-2828-7449")
        private String phoneNumber;    // 클라이언트에서 제공한 소셜 플랫폼의 user unique ID
    }

}
