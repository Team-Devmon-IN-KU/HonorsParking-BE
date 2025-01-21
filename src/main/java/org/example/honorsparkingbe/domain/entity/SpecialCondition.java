package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table( name = "specialCondition")


public class SpecialCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ParkingZone 연관 관계
    @OneToOne
    @JoinColumn(name = "parkingZoneId", unique = true, nullable = false)
    private ParkingZone parkingZone;

    // Car 연관 관계
    @OneToOne
    @JoinColumn(name = "carId", unique = true, nullable = false)
    private Car car;

    // 조건 이름
    private String conditionName;

    // 할인율 (예: "10%")
    private String discountRate;
}
