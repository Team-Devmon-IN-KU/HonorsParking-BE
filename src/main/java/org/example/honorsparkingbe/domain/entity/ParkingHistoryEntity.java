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
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.PaymentType;

@Getter
@Setter
@Entity
@Table(name = "parkingHistory")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ParkingHistoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "carId", nullable = false)
  private CarEntity carEntity;

  @ManyToOne
  @JoinColumn(name = "memberId", nullable = false)
  private MemberEntity memberEntity;

  @ManyToOne
  @JoinColumn(name = "parkingZoneId", nullable = false)
  private ParkingZoneEntity parkingZoneEntity;

  @ManyToOne
  @JoinColumn(name = "cardId")
  private CardEntity cardEntity;

  @Column(nullable = false)
  private LocalDateTime entranceTime;

  private LocalDateTime exitTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentType paymentType;

  @ManyToOne
  @JoinColumn(name = "payId")
  private PayEntity payEntity;

  private LocalDateTime deleteAt;
}
