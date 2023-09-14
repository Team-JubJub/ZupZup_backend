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
    private String storeImageUrl;
    private String openTime;
    private String closeTime;
    private String saleTimeStart;
    private String saleTimeEnd;

    private String isOpen;
    private String closeDay;

    private Integer starredUserCount;   // 찜한 사용자의 수

}
