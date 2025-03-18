package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mockito 환경에서 실행
public class IdDoubleCheckTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private JoinService joinService;

  @Test
  void id_already_exists() {
    // given
    String authId = "existingUser";
    when(memberRepository.existsByAuthId(authId)).thenReturn(true);

    // when
    boolean isAvailable = joinService.isAuthIdAvailable(authId);

    // then
    assertTrue(isAvailable);
  }


  @Test
  void id_not_exists() {
    // given
    String authId = "newUser";
    when(memberRepository.existsByAuthId(authId)).thenReturn(false);

    // when
    boolean isAvailable = joinService.isAuthIdAvailable(authId);

    // then
    assertFalse(isAvailable);
  }
}
