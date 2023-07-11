package dto.store.seller.request;

import domain.store.Store;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

public class StoreRequestDto {

    @Getter @Setter
    public static class patchDto {
        private String storeImageUrl; // 가게 대표 이미지 url - 이미지 없을 시 기본이미지
        private String openTime; // 운영 시작 시간
        private String closeTime; // 운영 마감 시간
        private String saleTimeStart;   // 할인 시작 시간
        private String saleTimeEnd; // 할인 마감 시간
    }

    @Getter @Setter
    public static class postDto {
        private String saleMatters; // 가게 공지사항
    }
}
