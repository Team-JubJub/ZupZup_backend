package domain.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor
@Table(name = "user", indexes = @Index(name = "idx_email", columnList = "email"))   // 나중에 customer, seller 구분해서 만들지 고민할 것
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;    // Encoded type, raw type 관련 고민하기

    @Enumerated(EnumType.STRING)
    private Provider provider;

}
