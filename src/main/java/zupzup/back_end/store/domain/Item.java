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
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long itemId;
        @Column(nullable = false, length = 20)
        private String itemName; // 상품명
        private String imageURL; // 상품 이미지
        @Column(nullable = false)
        private int itemPrice; // 상품 가격
        private int salePrice; // 할인된 가격
        private int itemCount; // 제품 개수
        @ManyToOne(optional = false) @JoinColumn(name = "storeId")
        private Store store; // 스토어 정보 ID

        public Item() {}

        // 상품 데이터를 업데이트 하는 로직
        public void updateItem(ItemDto itemDto) {

            this.itemName = itemDto.getItemName();
            this.imageURL = itemDto.getImageURL();
            this.itemPrice = itemDto.getItemPrice();
            this.salePrice = itemDto.getSalePrice();
            this.itemCount = itemDto.getItemCount();
            this.store = itemDto.getStore();
        }

}
