package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table( name ="pay")


public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 결제 고유 ID

    private int amount; // 결제된 금액

    private LocalDateTime paidAt; // 결제 시간

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id")
    private Member member; // 결제한 회원
}
