package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.Status;

@Getter
@Setter
@Entity
@Table( name = "unpaidAmount")


public class UnpaidAmountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 미납 금액 고유 ID

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id")
    private MemberEntity memberEntity; // 미납 금액을 가진 회원

    private int amount; // 미납 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 미납금 상태 (paid, unpaid)

    @ManyToOne
    @JoinColumn(name = "parkingHistoryId", referencedColumnName = "id", nullable = false)
    private ParkingHistoryEntity parkingHistoryEntity; // 미납 금액과 연결된 주차 기록
}

