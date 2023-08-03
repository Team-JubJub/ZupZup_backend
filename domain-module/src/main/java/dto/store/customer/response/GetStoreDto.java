package dto.store.customer.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetStoreDto {

    private Long storeId;

    private String storeName;
    private String category; // ex) 카페 / domain에 column 추가할 것.
    private String saleTimeStart;
    private String saleTimeEnd;
    private String salePercent;    // "00%"

}
