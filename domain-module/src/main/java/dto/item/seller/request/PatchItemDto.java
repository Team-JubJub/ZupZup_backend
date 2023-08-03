package dto.item.seller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "재로그인 요청 시 사용되는 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchItemDto {

    private Long itemId;
    private int itemCount;

}