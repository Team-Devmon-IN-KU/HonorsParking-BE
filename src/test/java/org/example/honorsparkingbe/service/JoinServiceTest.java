package org.example.honorsparkingbe.service;

/**
 * 회원가입 테스트 - authId 중복 시, 예외 발생
 */

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.dto.JoinDTO;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;

class JoinServiceTest {

  private MemberRepository memberRepository;
  private CarRepository carRepository;
  private BCryptPasswordEncoder passwordEncoder;
  private JoinService joinService;

  @BeforeEach
  void setUp() {
    memberRepository = Mockito.mock(MemberRepository.class);
    carRepository = Mockito.mock(CarRepository.class);
    passwordEncoder = Mockito.mock(BCryptPasswordEncoder.class);
    joinService = new JoinService(memberRepository, carRepository, passwordEncoder);
  }

  // 올바른 값일 경우
  @Test
  void joinProcess_ShouldSaveMemberAndCar_WhenValidInput() {
    // Given: JSON 데이터를 기반으로 JoinDTO 생성
    JoinDTO joinDTO = new JoinDTO();
    joinDTO.setPlatform("NORMAL");
    joinDTO.setMobile("01012341234");
    joinDTO.setName("아너스");
    joinDTO.setBirthyear("1990");
    joinDTO.setBirthday("01-01");
    joinDTO.setCarNumber("12가3456");
    joinDTO.setAccountId("user123");
    joinDTO.setAccountPassword("password123");
    joinDTO.setEmail("user@example.com");

    when(memberRepository.existsByAuthId("user123")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

    // When: JoinService의 joinProcess 호출
    joinService.joinProcess(joinDTO);

    // Then: CarRepository와 UserRepository가 올바르게 호출되었는지 검증
    verify(carRepository, times(1)).save(any(CarEntity.class));
    verify(memberRepository, times(1)).save(any(MemberEntity.class));
  }

  // 중복된 authId일 경우 예외 발생
  @Test
  void joinProcess_ShouldThrowException_WhenAuthIdAlreadyExists() {
    // Given
    JoinDTO joinDTO = new JoinDTO();
    joinDTO.setAccountId("existingUser");

    when(memberRepository.existsByAuthId("existingUser")).thenReturn(true);

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      joinService.joinProcess(joinDTO);
    });

    verify(memberRepository, times(1)).existsByAuthId("existingUser");
  }
}

