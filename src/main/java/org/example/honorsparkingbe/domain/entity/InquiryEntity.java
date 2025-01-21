package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table( name = "inquiry")


public class InquiryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 문의 고유 ID

    @ManyToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id")
    private MemberEntity memberEntity; // 문의한 회원

    private String chattingContent; // 문의 메시지 내용

    private LocalDateTime creationDate; // 문의 생성 날짜
}
