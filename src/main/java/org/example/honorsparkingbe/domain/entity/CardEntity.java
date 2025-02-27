package org.example.honorsparkingbe.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "card")

public class CardEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 카드 고유 ID

  @ManyToOne
  @JoinColumn(name = "memberId", referencedColumnName = "id", unique = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private MemberEntity memberEntity; // Member와 1:1 관계

  private String token; // PG사 결제에 필요한 토큰

  private String cardCompany; // 카드 회사

  private String cardNickname; // 카드 별명

  private String hashedCardNumber; // 해쉬된 카드 넘버
}


