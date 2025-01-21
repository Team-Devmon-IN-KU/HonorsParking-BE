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


public class ParkingHistory {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "carId", unique = true, nullable = false)
        private Car car;

        @OneToOne
        @JoinColumn(name = "memberId", unique = true, nullable = false)
        private Member member;

        @ManyToOne
        @JoinColumn(name = "parkingZoneId", unique = true, nullable = false)
        private ParkingZone parkingZone;

        @OneToOne
        @JoinColumn(name = "cardId", unique = true)
        private Card card;

        @Column(nullable = false)
        private LocalDateTime entranceTime;

        private LocalDateTime exitTime;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private PaymentType paymentType;

        @ManyToOne
        @JoinColumn(name = "payId", nullable = false)
        private Pay pay;
}
