package org.example.honorsparkingbe.repository.internal;

import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Long> {

  // 특정 회원의 모든 알람 조회
  Page<AlarmEntity> findByMemberEntityId(Long memberId, Pageable pageable);

  // 특정 회원의 특정 알람 유형 조회
  Page<AlarmEntity> findByMemberEntityIdAndAlarmType(Long memberId, AlarmType alarmType,
      Pageable pageable);

  // 여러 알람 ID를 읽음 상태로 업데이트 (READ 상태가 아닌 알람만)
  @Modifying
  @Query("UPDATE AlarmEntity a SET a.isRead = 'READ', a.readAt = CURRENT_TIMESTAMP WHERE a.id IN :ids AND a.isRead = 'UNREAD'")
  int updateAlarmsToRead(@Param("ids") List<Long> ids);

  // 이미 읽은 알람 ID 조회
  @Query("SELECT a.id FROM AlarmEntity a WHERE a.id IN :ids AND a.isRead = 'READ'")
  List<Long> findReadAlarms(@Param("ids") List<Long> ids);

  // 알람 선택 삭제
  @Modifying
  @Query("DELETE FROM AlarmEntity a WHERE a.id IN :ids")
  int deleteAlarms(@Param("ids") List<Long> ids);

  // 알람 전체 삭제
  @Modifying
  @Query("DELETE FROM AlarmEntity a WHERE a.memberEntity.id = :memberId")
  int deleteAllAlarmsByMemberId(@Param("memberId") Long memberId);
}
