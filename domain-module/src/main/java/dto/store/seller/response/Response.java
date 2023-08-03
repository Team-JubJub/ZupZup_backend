package dto.store.seller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Response {

    private String storeName;
    private String openTime;
    private String endTime;

    private String saleMatters;

    private String saleTimeStart;
    private String saleTimeEnd;

    public String closedDay;

}
