package org.example.honorsparkingbe.unit.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.domain.enums.IsRead;
import org.example.honorsparkingbe.repository.internal.AlarmRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.junit.jupiter.api.Assertions;

public class AlarmServiceTest {

  @Mock
  private AlarmRepository alarmRepository;

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private AlarmService alarmService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  /**
   * 1. 알람 목록 조회 테스트
   */
  @Test
  @DisplayName("알람 목록 조회")
  void testGetAlarms_Success() {
    // Given
    Long memberId = 1L;
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    List<AlarmEntity> alarmEntities = List.of(
        createAlarm(1L, "테스트 알람", IsRead.UNREAD, AlarmType.INOUT),
        createAlarm(2L, "다른 알람", IsRead.READ, AlarmType.RESERVE)
    );
    Page<AlarmEntity> alarmPage = new PageImpl<>(alarmEntities, pageable, alarmEntities.size());

    when(alarmRepository.findByMemberEntityId(memberId, pageable)).thenReturn(alarmPage);

    // When
    Map<String, Object> response = alarmService.getAlarms(memberId, null, 0, 10);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.get("alarms")).isInstanceOf(List.class);
    assertThat(((List<?>) response.get("alarms")).size()).isEqualTo(2);
  }

  /**
   * 2. 존재하지 않는 카테고리로 요청 시 예외 발생
   */
  @Test
  @DisplayName("존재하지 않는 카테고리로 알람 조회 시 예외 발생")
  void testGetAlarms_InvalidCategory_ShouldThrowException() {
    // Given
    Long memberId = 1L;

    // When & Then
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        alarmService.getAlarms(memberId, "INVALID", 0, 10));
  }

  /**
   * 3. 알람이 없을 때 빈 리스트 반환
   */
  @Test
  @DisplayName("알람이 없을 때 빈 리스트 반환")
  void testGetAlarms_NoAlarms_ShouldReturnEmptyList() {
    // Given
    Long memberId = 1L;
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    Page<AlarmEntity> emptyPage = new PageImpl<>(List.of(), pageable, 0);
    when(alarmRepository.findByMemberEntityId(memberId, pageable)).thenReturn(emptyPage);

    // When
    Map<String, Object> response = alarmService.getAlarms(memberId, null, 0, 10);

    // Then
    assertThat(((List<?>) response.get("alarms")).isEmpty()).isTrue();
  }

  /**
   * 4. 알람 읽기
   */
  @Test
  @DisplayName("알람 읽기")
  void testUpdateAlarmsToRead_Success() {
    // Given
    List<Long> alarmIds = List.of(1L, 2L, 3L);

    when(alarmRepository.findReadAlarms(alarmIds)).thenReturn(List.of(1L)); // 1번은 이미 읽음
    when(alarmRepository.updateAlarmsToRead(anyList())).thenReturn(2); // 2, 3번이 읽음 처리됨

    // When
    Map<String, Object> response = alarmService.updateAlarmsToRead(alarmIds);

    // Debugging
    System.out.println("테스트 실행 결과: " + response);

    // Then
    assertThat(response.get("updatedIds")).isNotNull(); // updatedIds가 존재해야 함
    assertThat(((List<?>) response.get("updatedIds")).size()).isEqualTo(2); // 2,3번이 읽음 처리됨
    assertThat(((List<?>) response.get("alreadyReadIds")).size()).isEqualTo(1); // 1번은 이미 읽음
    assertThat(response.get("success")).isEqualTo(
        !((List<?>) response.get("updatedIds")).isEmpty()); // 읽음 처리된 게 있으면 success=true
  }

  /**
   * 5. 알람 삭제
   */
  @Test
  @DisplayName("알람 삭제")
  void testDeleteAlarms_Success() {
    // Given
    List<Long> alarmIds = List.of(1L, 2L);
    when(alarmRepository.deleteAlarms(alarmIds)).thenReturn(2);

    // When
    Map<String, Object> response = alarmService.deleteAlarms(alarmIds);

    // Then
    assertThat(response.get("success")).isEqualTo(true);
    assertThat(((List<?>) response.get("deletedIds")).size()).isEqualTo(2);
  }

  /**
   * 6. 특정 유저의 모든 알람 삭제
   */
  @Test
  @DisplayName("특정 유저의 모든 알람 삭제")
  void testDeleteAllAlarms_Success() {
    // Given
    Long memberId = 1L;
    when(alarmRepository.deleteAllAlarmsByMemberId(memberId)).thenReturn(5);

    // When
    Map<String, Object> response = alarmService.deleteAllAlarms(memberId);

    // Then
    assertThat(response.get("success")).isEqualTo(true);
    assertThat(response.get("deletedCount")).isEqualTo(5);
  }

  /**
   * 7. 회원 ID 조회
   */
  @Test
  @DisplayName("회원 ID 조회")
  void testFindMemberIdByAuthId_Success() {
    // Given
    String authId = "testAuth";
    MemberEntity mockMember = new MemberEntity();
    mockMember.setId(1L);
    when(memberRepository.findByAuthId(authId)).thenReturn(mockMember);

    // When
    Long memberId = alarmService.findMemberIdByAuthId(authId);

    // Then
    assertThat(memberId).isEqualTo(1L);
  }

  /**
   * 페이지네이션 적용 여부 확인
   */
  @Test
  @DisplayName("페이지네이션 적용 여부 확인")
  void testGetAlarms_Pagination() {
    // Given
    Long memberId = 1L;
    Pageable pageable = PageRequest.of(1, 5, Sort.by("createdAt").descending()); // 1페이지, 5개씩 가져오기

    List<AlarmEntity> alarmEntities = List.of(
        createAlarm(6L, "알람1", IsRead.UNREAD, AlarmType.INOUT),
        createAlarm(7L, "알람2", IsRead.UNREAD, AlarmType.RESERVE)
    );
    Page<AlarmEntity> alarmPage = new PageImpl<>(alarmEntities, pageable, 10); // 총 10개 데이터 중 일부 조회

    when(alarmRepository.findByMemberEntityId(memberId, pageable)).thenReturn(alarmPage);

    // When
    Map<String, Object> response = alarmService.getAlarms(memberId, null, 1, 5);

    // Then
    assertThat(response.get("pagination")).isNotNull();
    assertThat(((Map<String, Object>) response.get("pagination")).get("currentPage")).isEqualTo(1);
    assertThat(((List<?>) response.get("alarms")).size()).isEqualTo(2);
  }

  /**
   * 도우미 메서드: 알람 생성 테스트 코드에서 반복되는 AlarmEntity 생성 로직을 줄이기 위해 만들어짐
   */
  private AlarmEntity createAlarm(Long id, String content, IsRead isRead, AlarmType alarmType) {
    AlarmEntity alarm = new AlarmEntity();
    alarm.setId(id);
    alarm.setContent(content);
    alarm.setIsRead(isRead);
    alarm.setAlarmType(alarmType);
    return alarm;
  }
}
