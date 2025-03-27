package org.example.honorsparkingbe.service;

/**
 * 사용자 인증 객체 테스트 - 올바른 authId가 주어졌을 때, 올바른 CustomUserDetails를 반환하는지 - 잘못된 authId가 주어졌을 때, 올바른
 * CustomUserDetails를 반환하는지
 */

import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

  private MemberRepository memberRepository;
  private CustomUserDetailsService userDetailsService;
  private CustomUserDetails customUserDetails;

  @BeforeEach
  void setUp() {
    // Mock UserRepository 생성
    memberRepository = Mockito.mock(MemberRepository.class);
    // CustomUserDetailsService 초기화
    userDetailsService = new CustomUserDetailsService(memberRepository);

    MemberEntity customUser = new MemberEntity();
    customUser.setAuthId("authId");
    customUser.setPassword("password123");
    customUser.setRole(MemberRole.ROLE_USER);
    customUser.setId(1L);
    customUserDetails = new CustomUserDetails(customUser);
  }

  @Test
  void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
    // Given
    String authId = "existingUser";
    MemberEntity mockUser = new MemberEntity();
    mockUser.setAuthId(authId);
    mockUser.setPassword("password123");
    mockUser.setRole(MemberRole.ROLE_USER);
    mockUser.setId(1L);

    when(memberRepository.findByAuthId(authId)).thenReturn(mockUser);

    // When
    CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(
        authId);

    // Then
    assertNotNull(userDetails);
    assertEquals(authId, userDetails.getUsername());
    assertEquals("password123", userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    verify(memberRepository, times(1)).findByAuthId(authId);
  }

  @Test
  void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
    // Given
    String authId = "nonExistentUser";
    when(memberRepository.findByAuthId(authId)).thenReturn(null);

    // When & Then
    UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
      userDetailsService.loadUserByUsername(authId);
    });

    assertEquals("User not found with authId: " + authId, exception.getMessage());
    verify(memberRepository, times(1)).findByAuthId(authId);
  }

  @Test
  void getId_ShouldReturnMemberId_WhenMemberExists() {
    // When
    Long memberId = customUserDetails.getId();

    // Then
    assertNotNull(memberId);
    assertEquals(1L, memberId);
  }

  @Test
  void getId_ShouldThrowNullPointerException_WhenMemberIsNull() {
    // Given
    CustomUserDetails userDetailsWithNullMember = new CustomUserDetails(null);

    // When & Then
    assertThrows(NullPointerException.class, userDetailsWithNullMember::getId);
  }
}
