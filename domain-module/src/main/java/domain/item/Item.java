package domain.item;

import domain.store.Store;
import dto.item.ItemDto;
import dto.item.seller.request.UpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "item")
@Getter
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

    // 상품 데이터를 업데이트 하는 로직
        public void updateItem(UpdateRequestDto itemDto) {

            this.itemName = itemDto.getItemName();
            this.imageURL = itemDto.getImageURL();
            this.itemPrice = itemDto.getItemPrice();
            this.salePrice = itemDto.getSalePrice();
            this.itemCount = itemDto.getItemCount();
        }

        public void saveItem(ItemDto itemDto) {

            this.itemName = itemDto.getItemName();
            this.imageURL = itemDto.getImageURL();
            this.itemPrice = itemDto.getItemPrice();
            this.salePrice = itemDto.getSalePrice();
            this.itemCount = itemDto.getItemCount();
            this.store = itemDto.getStore();
        }

    // 상품 개수 변경(사장님이 예약 확정 시) -> setter로 갈지 이 함수로 갈지는 나중에 결정.
        public void updateItemCount(ItemDto itemDto) {
            this.itemCount = itemDto.getItemCount();
        }
}
