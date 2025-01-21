package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table( name = "specialCondition")


public class SpecialConditionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ParkingZone 연관 관계
    @OneToOne
    @JoinColumn(name = "parkingZoneId", unique = true, nullable = false)
    private ParkingZoneEntity parkingZoneEntity;

    // Car 연관 관계
    @OneToOne
    @JoinColumn(name = "carId", unique = true, nullable = false)
    private CarEntity carEntity;

    // 조건 이름
    private String conditionName;

    // 할인율 (예: "10%")
    private String discountRate;
}
