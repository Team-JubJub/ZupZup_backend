package dto.auth.token.customer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Schema(description = "액세스 토큰 갱신 요청의 결과와 정보를 담은 DTO")
@Getter
@ToString
public class SellerRefreshResultDto {

    @Schema(description = "요청 처리 결과", example = "success")
    private String result;
    @Schema(description = "요청 처리 결과에 대한 메세지", example = "Access token refreshed(액세스 토큰 갱신 성공)")
    private String message;
    @Schema(description = "줍줍의 유저 식별자('소셜플랫폼 명_소셜플랫폼에서 제공한 uniqueID' 포맷)", example = "NAVER_im1from2naver3")
    private String providerUserId;
    @Schema(description = "갱신된 액세스 토큰")
    private String accessToken;

    public SellerRefreshResultDto(String result, String message, String providerUserId, String accessToken) {
        this.result = result;
        this.message = message;
        this.providerUserId = providerUserId;
        this.accessToken = accessToken;
    }

}
