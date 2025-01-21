package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.AlarmStatus;
import org.example.honorsparkingbe.domain.enums.AlarmType;


import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table( name = "alarm")
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 알람 고유 ID

    @OneToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id", unique = true)
    private Member member; // Member와 1:1 관계

    private String content; // 알람 내용

    @Enumerated(EnumType.STRING)
    private AlarmStatus status; // 알람 상태 (UNREAD, READ)

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType; // 알람 유형 (INOUT, RESERVE, PAYMENT)

    private LocalDateTime createdAt; // 생성 시간

    private LocalDateTime readAt; // 읽은 시간 (null일 수 있음)
}
