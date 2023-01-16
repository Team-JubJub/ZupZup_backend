package zupzup.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zupzup.back_end.domain.ItemImg;

@Repository
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
}