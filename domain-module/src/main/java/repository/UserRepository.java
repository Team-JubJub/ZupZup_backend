package repository;

import domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderUserId(String providerUserId); //isPresent() 사용 위해 Optional<> 타입으로 선언
    Optional<User> findByPhoneNumber(String phoneNumber);

}
