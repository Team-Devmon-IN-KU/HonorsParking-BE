package org.example.honorsparkingbe.domain.entity;

import jakarta.persistence.*;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;

@Entity
@Table( name = "member")

public class MemberEntity {

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

    @Column(nullable = false, length = 60)
    private String userName;

    @Column(nullable = false, unique = true)
    private String authId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private int birthdayYear; // 생년 (정수형)

    @Column(nullable = false, length = 4)
    private String birthday; // 생일 (문자열, "MMDD" 형태로 저장할 수 있음)

}
