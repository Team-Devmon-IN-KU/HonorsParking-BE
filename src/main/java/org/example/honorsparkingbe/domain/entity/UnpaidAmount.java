package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.Status;

@Getter
@Setter
@Entity
@Table( name = "unpaidAmount")


public class UnpaidAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 미납 금액 고유 ID

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id")
    private Member member; // 미납 금액을 가진 회원

    private int amount; // 미납 금액

    @Enumerated(EnumType.STRING)
    private Status status; // 미납금 상태 (paid, unpaid)
}

