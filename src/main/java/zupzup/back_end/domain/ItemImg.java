package zupzup.back_end.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "itemImg")
public class ItemImg {

    @Id
    @Column(name = "itemImgId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String imgName; //이미지 파일명
    private String oriImgName; //원본 이미지 파일명
    private String imgUrl; //이미지 조회 경로

    // 상품 엔티티와 일대일 단방향 관계로 매핑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itemId")
    private Item item;

    public void updateItemImg(String imgName, String oriImgName, String imgUrl) {
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
    }
}
