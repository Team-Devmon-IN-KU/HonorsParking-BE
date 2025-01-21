package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.PaymentType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table( name = "parkingHistory")


public class ParkingHistoryEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "carId", unique = true, nullable = false)
        private CarEntity carEntity;

        @OneToOne
        @JoinColumn(name = "memberId", unique = true, nullable = false)
        private MemberEntity memberEntity;

        @ManyToOne
        @JoinColumn(name = "parkingZoneId", unique = true, nullable = false)
        private ParkingZoneEntity parkingZoneEntity;

        @OneToOne
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
}
