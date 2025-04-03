package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pay")

public class PayEntity {

  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY) BulkInsert를 구현하기 위한 id값 정의 X
  private Long id; // 결제 고유 ID 이지만 입차 ID와 동일한 값을 쓸 예정 한 입출차에서는 결제완료가 1번이기 때문에

//  private Long entryId; // 입차 ID값

  private int amount; // 결제된 금액

  private LocalDateTime paidAt; // 결제 시간

  @ManyToOne
  @JoinColumn(name = "memberId", referencedColumnName = "id")
  private MemberEntity memberEntity; // 결제한 회원
}
