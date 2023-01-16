package zupzup.back_end.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zupzup.back_end.store.domain.ItemImg;

@Repository
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
}