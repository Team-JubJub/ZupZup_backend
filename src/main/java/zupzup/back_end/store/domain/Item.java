package zupzup.back_end.store.domain;

import jakarta.persistence.*;
import lombok.Data;
import zupzup.back_end.store.dto.ItemDto;

@Entity
@Data
@Table(name = "item")
public class Item {

        @Id
        @Column(name = "itemId")
        @GeneratedValue(strategy = GenerationType.IDENTITY) //mysql의 increment 방식이 identity이므로 값을 identity로 변경해줘야 함.
        private Long id;
        @Column(nullable = false, length = 20)
        private String itemName; // 상품명
        @Column(nullable = false)
        private int itemPrice; // 상품 가격
        private int discountedPrice; // 할인된 가격
        private int count; // 제품 개수
        @ManyToOne(optional = false) @JoinColumn(name = "storeId")
        private Store store; // 스토어 정보 ID

        protected Item() {}

        // 상품 데이터를 업데이트 하는 로직
        public void updateItem(ItemDto itemDto) {

            this.itemName = itemDto.getItemName();
            this.itemPrice = itemDto.getItemPrice();
            this.discountedPrice = itemDto.getDiscountedPrice();
            this.count = itemDto.getCount();
            this.store = itemDto.getStore();
        }

}
