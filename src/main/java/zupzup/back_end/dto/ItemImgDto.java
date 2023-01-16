package zupzup.back_end.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import zupzup.back_end.domain.ItemImg;

@Getter
@Setter
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemImgDto of(ItemImg itemImg) {
        //entity 를 파라미터로 받아 dto 로 반환
        return modelMapper.map(itemImg, ItemImgDto.class);
    }
}