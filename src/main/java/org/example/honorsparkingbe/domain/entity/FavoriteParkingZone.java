package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table( name = "favoriteParkingZone")


public class FavoriteParkingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ParkingZone 연관 관계
    @OneToOne
    @JoinColumn(name = "parkingZoneId", unique = true, nullable = false)
    private ParkingZone parkingZone;

    // Member 연관 관계
    @OneToOne
    @JoinColumn(name = "memberId", unique = true, nullable = false)
    private Member member;
}
