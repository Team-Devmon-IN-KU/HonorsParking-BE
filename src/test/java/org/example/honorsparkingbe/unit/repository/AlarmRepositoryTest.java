package org.example.honorsparkingbe.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.domain.enums.IsRead;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.repository.internal.AlarmRepository;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@EntityScan(basePackages = "org.example.honorsparkingbe.domain.entity")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlarmRepositoryTest {

  @Autowired
  private AlarmRepository alarmRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private CarRepository carRepository;

  private MemberEntity testMember;

  @BeforeEach
  void setUp() {
    // 먼저 CarEntity를 저장 (필수로 넣어야 하는 값만 채움)
    CarEntity testCar = carRepository.save(CarEntity.builder()
        .carNumber("12가3456")
        .carType(null) // nullable이므로 생략 가능
        .isElectric(false)
        .entranceTime(LocalDateTime.now())
        .build());

    // MemberEntity 필수 값들 모두 설정
    testMember = memberRepository.save(
        MemberEntity.builder()
            .authId("testAuthId")
            .loginPlatform(LoginPlatform.NORMAL)
            .role(MemberRole.ROLE_USER)
            .userName("테스트 유저")
            .password("testPassword")
            .phoneNumber("01012345678")
            .email("test@example.com")
            .birthdayYear(1999)
            .birthday("0101")
            .carEntity(testCar) // 필수로 넣어야 함
            .build()
    );
    alarmRepository.saveAll(List.of(
        createAlarm(testMember, "첫 번째 알람", IsRead.UNREAD, AlarmType.INOUT),
        createAlarm(testMember, "두 번째 알람", IsRead.READ, AlarmType.RESERVE)
    ));
  }

  /**
   * 도우미 메서드 - 알람 생성
   */
  private AlarmEntity createAlarm(MemberEntity member, String content, IsRead isRead,
      AlarmType alarmType) {
    return alarmRepository.save(AlarmEntity.builder()
        .memberEntity(member)
        .content(content)
        .isRead(isRead)
        .alarmType(alarmType)
        .createdAt(LocalDateTime.now())
        .build());
  }


  /**
   * 1. 특정 회원의 알람 조회 (페이지네이션 포함)
   */
  @Test
  @DisplayName("특정 회원의 알람 조회")
  void testFindByMemberEntityId() {
    // When
    List<AlarmEntity> alarms = alarmRepository.findByMemberEntityId(testMember.getId(), null)
        .getContent();

    // Then
    assertThat(alarms).hasSize(2);
    assertThat(alarms.get(0).getContent()).isEqualTo("첫 번째 알람");
  }

  /**
   * 2. 특정 회원의 특정 알람 유형 조회
   */
  @Test
  @DisplayName("특정 회원의 특정 알람 유형 조회")
  void testFindByMemberEntityIdAndAlarmType() {
    // When
    List<AlarmEntity> alarms = alarmRepository.findByMemberEntityIdAndAlarmType(testMember.getId(),
        AlarmType.INOUT, null).getContent();

    // Then
    assertThat(alarms).hasSize(1);
    assertThat(alarms.get(0).getAlarmType()).isEqualTo(AlarmType.INOUT);
  }

  /**
   * 3. 읽음 처리된 알람 조회
   */
  @Test
  @DisplayName("읽음 처리된 알람 조회")
  void testFindReadAlarms() {
    // Given
    List<Long> alarmIds = alarmRepository.findAll().stream().map(AlarmEntity::getId).toList();

    // When
    List<Long> readAlarms = alarmRepository.findReadAlarms(alarmIds);

    // Then
    assertThat(readAlarms).hasSize(1);
  }

  /**
   * 4. 여러 개의 알람 읽음 처리
   */
  @Test
  @DisplayName("알람 읽음 처리")
  void testUpdateAlarmsToRead() {
    // Given
    List<Long> unreadAlarmIds = alarmRepository.findAll().stream()
        .filter(alarm -> alarm.getIsRead() == IsRead.UNREAD)
        .map(AlarmEntity::getId)
        .toList();

    // When
    int updatedCount = alarmRepository.updateAlarmsToRead(unreadAlarmIds);

    // Then
    assertThat(updatedCount).isEqualTo(1); // UNREAD → READ 처리된 건 1개
  }

  /**
   * 5. 특정 알람 삭제
   */
  @Test
  @DisplayName("특정 알람 삭제")
  void testDeleteAlarms() {
    // Given
    List<Long> alarmIds = alarmRepository.findAll().stream().map(AlarmEntity::getId).toList();

    // When
    int deletedCount = alarmRepository.deleteAlarms(alarmIds);

    // Then
    assertThat(deletedCount).isEqualTo(2); // 2개 삭제 성공
    assertThat(alarmRepository.findAll()).isEmpty(); // 삭제 후 남은 데이터가 없음
  }

  /**
   * 6. 특정 회원의 모든 알람 삭제
   */
  @Test
  @DisplayName("특정 회원의 모든 알람 삭제")
  void testDeleteAllAlarmsByMemberId() {
    // When
    int deletedCount = alarmRepository.deleteAllAlarmsByMemberId(testMember.getId());

    // Then
    assertThat(deletedCount).isEqualTo(2);
    assertThat(alarmRepository.findByMemberEntityId(testMember.getId(), null)
        .getTotalElements()).isEqualTo(0);
  }

}
