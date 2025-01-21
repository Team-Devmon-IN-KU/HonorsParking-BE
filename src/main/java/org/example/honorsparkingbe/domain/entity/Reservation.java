package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.ReservationState;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table( name = "reservation")


public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예약 고유 ID

    @ManyToOne
    @JoinColumn(name = "parkingZoneId", referencedColumnName = "id")
    private ParkingZone parkingZone; // 예약한 주차장

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id")
    private Member member; // 예약한 회원

    @ManyToOne
    @JoinColumn(name = "carId", referencedColumnName = "id")
    private Car car; // 예약한 차량

    @Enumerated(EnumType.STRING)
    private ReservationState reservationState; // 예약 상태 (NOSHOW, ENTER, CANCELED)

    private LocalDateTime expireAt; // 예약 만료 시간

    private LocalDateTime createdAt; // 예약 생성 시간
}

