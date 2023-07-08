package domain.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor
@Table(name = "firstOrderData")
public class FirstOrderData {

    @Id
    @Column(name = "firstOrderDataId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long firstOrderDataId;    // auto increment id

    @Column(nullable = false) private String registerTime;
    @Column(nullable = false) private String firstOrderTime;

}
