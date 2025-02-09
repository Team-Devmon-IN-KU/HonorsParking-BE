package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MemberEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "carId", unique = true)
  private CarEntity carEntity;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LoginPlatform loginPlatform;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MemberRole role;

  private String userName;

  @Column(nullable = false, unique = true)
  private String authId;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, length = 12)
  private String phoneNumber;

  private String email;

  private int birthdayYear; // 생년 (정수형)

  @Column(length = 4)
  private String birthday; // 생일 (문자열, "MMDD" 형태로 저장할 수 있음)

}
