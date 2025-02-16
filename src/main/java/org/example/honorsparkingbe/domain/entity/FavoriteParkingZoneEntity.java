package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "favoriteParkingZone",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"memberId", "parkingZoneId"})
    }
)
@Builder

public class FavoriteParkingZoneEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ParkingZone 연관 관계
  @ManyToOne
  @JoinColumn(name = "parkingZoneId", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ParkingZoneEntity parkingZoneEntity;

  // Member 연관 관계
  @ManyToOne
  @JoinColumn(name = "memberId", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private MemberEntity memberEntity;
}
