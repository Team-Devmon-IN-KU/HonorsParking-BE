package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.honorsparkingbe.domain.enums.PaymentType;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table( name = "parkingHistory")
@NoArgsConstructor @AllArgsConstructor
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
        @JoinColumn(name = "cardId", unique = true)
        private CardEntity cardEntity;

        @Column(nullable = false)
        private LocalDateTime entranceTime;

        private LocalDateTime exitTime;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PaymentType paymentType;

        @ManyToOne
        @JoinColumn(name = "payId", nullable = false)
        private PayEntity payEntity;

        private LocalDateTime deleteAt;
}
