package dto.info.customer.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PatchOptionalTermDto {

    @Schema(description = "변경하고자 하는 약관의 동의 여부", allowableValues = {"true", "false"})
    Boolean optionalTerm1;

}
