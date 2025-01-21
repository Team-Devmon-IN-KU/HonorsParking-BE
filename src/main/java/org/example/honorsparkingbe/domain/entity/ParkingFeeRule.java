package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.honorsparkingbe.domain.enums.CarType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table( name = "parkingFeeRule")


public class ParkingFeeRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ParkingZone 연관 관계
    @OneToOne
    @JoinColumn(name = "parkingZoneId", unique = true, nullable = false)
    private ParkingZone parkingZone;

    // 차량 타입 (Enum)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarType carType;

    // 요금 규칙 이름
    private String ruleName;

    // 측정 시작 시간
    private Integer startTime;

    // 측정 종료 시간
    private Integer endTime;

    // 단위 시간당 부과 요금
    private Integer costPerTimeSlot;

    // 단위 시간
    private Integer costTimeSlot;
}



