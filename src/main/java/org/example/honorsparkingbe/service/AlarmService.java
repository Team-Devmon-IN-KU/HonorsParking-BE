package org.example.honorsparkingbe.service;

import jakarta.transaction.Transactional;
import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.dto.AlarmResponse;
import org.example.honorsparkingbe.repository.internal.AlarmRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AlarmService {

  private final AlarmRepository alarmRepository;
  private final MemberRepository memberRepository;

  public AlarmService(AlarmRepository alarmRepository, MemberRepository memberRepository) {
    this.alarmRepository = alarmRepository;
    this.memberRepository = memberRepository;
  }

  // 회원 알람 불러오기
  // GET /api/v1/alarmAll
  public Map<String, Object> getAlarms(Long memberId, String category, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

    Page<AlarmEntity> alarmPage;
    if (category != null) {
      boolean isValidCategory = Arrays.stream(AlarmType.values())
          .anyMatch(type -> type.name().equalsIgnoreCase(category));

      if (!isValidCategory) {
        throw new IllegalArgumentException("Invalid category value: " + category);
      }

      AlarmType alarmType = AlarmType.valueOf(category.toUpperCase());
      alarmPage = alarmRepository.findByMemberEntityIdAndAlarmType(memberId, alarmType, pageable);
    } else {
      alarmPage = alarmRepository.findByMemberEntityId(memberId, pageable);
    }

    List<AlarmResponse> alarmList = alarmPage.getContent().stream()
        .map(AlarmResponse::new)
        .collect(Collectors.toList());

    return Map.of(
        "alarms", alarmList,
        "pagination", Map.of(
            "currentPage", alarmPage.getNumber(),
            "totalPages", alarmPage.getTotalPages(),
            "pageSize", alarmPage.getSize(),
            "totalItems", alarmPage.getTotalElements()
        )
    );
  }

  // 회원 알람 읽기
  // PUT /api/v1/alarm
  @Transactional
  public Map<String, Object> updateAlarmsToRead(List<Long> alarmIDList) {
    if (alarmIDList == null || alarmIDList.isEmpty()) {
      throw new IllegalArgumentException("alarmIDList cannot be null or empty");
    }

    // 1. 이미 READ 상태인 알람 조회
    List<Long> alreadyReadAlarms = alarmRepository.findReadAlarms(alarmIDList);

    // 2. UNREAD 상태인 알람만 추출
    List<Long> unreadAlarms = alarmIDList.stream()
        .filter(id -> !alreadyReadAlarms.contains(id))  // READ 상태인 알람 제외
        .collect(Collectors.toList());

    if (unreadAlarms.isEmpty()) {
      // 모든 알람이 READ 상태라면 예외 발생시키지 않고 success=false 반환
      return Map.of(
          "success", false,
          "message", "All selected alarms are already read",
          "updatedIds", List.of()
      );
    }

    // 3. UNREAD 상태인 알람만 READ로 변경
    int updatedCount = alarmRepository.updateAlarmsToRead(alarmIDList);

    // 응답 데이터 구성
    return Map.of(
        "success", updatedCount > 0,
        "updatedIds", unreadAlarms,
        "alreadyReadIds", alreadyReadAlarms  // 이미 읽은 알람 리스트 포함
    );
  }

  // 회원 알람 선택 삭제
  // DELETE /api/v1/alarm
  @Transactional
  public Map<String, Object> deleteAlarms(List<Long> alarmIDList) {
    if (alarmIDList == null || alarmIDList.isEmpty()) {
      throw new IllegalArgumentException("alarmIDList cannot be null or empty");
    }

    // 삭제 실행
    int deletedCount = alarmRepository.deleteAlarms(alarmIDList);

    // 응답 데이터 구성
    return Map.of(
        "success", deletedCount > 0,
        "deletedIds", deletedCount > 0 ? alarmIDList : List.of()
    );
  }

  // 회원 알람 전체 삭제
  // DELETE /api/v1/alarm/all
  @Transactional
  public Map<String, Object> deleteAllAlarms(Long memberId) {
    if (memberId == null) {
      throw new IllegalArgumentException("memberId cannot be null");
    }

    // 삭제 실행
    int deletedCount = alarmRepository.deleteAllAlarmsByMemberId(memberId);

    // 응답 데이터 구성
    return Map.of(
        "success", deletedCount > 0,
        "deletedCount", deletedCount
    );
  }

  /**
   * 로그인 사용자의 authId로 memberId 조회
   *
   * @param authId 로그인할 때 사용한 유저 ID
   * @return memberId (Long 타입)
   */
  public Long findMemberIdByAuthId(String authId) {
    MemberEntity member = memberRepository.findByAuthId(authId);
    if (member == null) {
      throw new IllegalStateException("User not found with authId: " + authId);
    }
    return member.getId();
  }
}
