package org.example.honorsparkingbe.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table( name = "card")


public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 카드 고유 ID

    @OneToOne
    @JoinColumn(name = "memberId", referencedColumnName = "id", unique = true)
    private MemberEntity memberEntity; // Member와 1:1 관계

    private String token; // PG사 결제에 필요한 토큰

    private String cardCompany; // 카드 회사

    private String cardNickname; // 카드 별명
}


