package repository;

import domain.auth.Seller.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    Seller findSellerByLoginId(String loginId);   // 테스트용 임시 함수

}
