package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.honorsparkingbe.domain.enums.CarType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table( name = "car")


public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 입차 시간
    private LocalDateTime entranceTime;

    // 차량 번호
    @Column(nullable = false)
    private String carNumber;

    // 차종 (Enum)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarType carType;

    // 전기차 여부
    private boolean isElectric;
}
