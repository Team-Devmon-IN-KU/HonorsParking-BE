package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parkingFeeRule")
@Builder

public class ParkingFeeRuleEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ParkingZone 연관 관계
  @ManyToOne
  @JoinColumn(name = "parkingZoneId", nullable = false)  // 외래 키 설정
  @OnDelete(action = OnDeleteAction.CASCADE)  // 외래키 삭제 시 CASCADE 처리
  private ParkingZoneEntity parkingZoneEntity;

  // 차량 타입 (Enum)
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CarType carType;

  // 요금 규칙 이름
  private String ruleName;

  // 측정 시작 시간
  @Column(nullable = false)
  private Integer startTime;

  // 측정 종료 시간 null이 라면 마지막 측정시간
  @Column(nullable = true)
  private Integer endTime;

  // 단위 시간당 부과 요금
  @Column(nullable = false)
  private Integer costPerTimeSlot;

  // 단위 시간
  @Column(nullable = false)
  private Integer costTimeSlot;
}



