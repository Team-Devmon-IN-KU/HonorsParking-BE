package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table( name ="pay")

public class PayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 결제 고유 ID

    private int amount; // 결제된 금액

    private LocalDateTime paidAt; // 결제 시간

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id")
    private MemberEntity memberEntity; // 결제한 회원
}
