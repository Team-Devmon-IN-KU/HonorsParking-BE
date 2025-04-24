package org.example.honorsparkingbe.performanceTest.createData;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.AlarmEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.AlarmType;
import org.example.honorsparkingbe.domain.enums.IsRead;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("performanceTest")
@DependsOn({"createParkingZonesInPerformanceTest", "createUsersInPerformanceTest"})
public class CreateAlarmsInPerformanceTest extends PerformanceCheckInit {

  @PostConstruct
  @Transactional
  /**
   * 유저 a,b,c기반의 Alarm을 랜덤하게 120개 만듭니다.
   * 	이때 알람종류 3가지도 균등하게 들어가야 합니다.
   *  이때 각 3가지에 대해서 읽은 알람 안읽은 알람을 반반 나눕니다
   */
  public void insertAlarmsDataForPerformanceTest() {

    List<AlarmEntity> alarms = new ArrayList<>();

    // 유저 3명
    List<MemberEntity> members = memberRepository.findAll();
    if (members.size() < 3) {
      throw new IllegalStateException("유저 a, b, c 최소 3명 필요");
    }

    List<MemberEntity> selectedMembers = members.subList(0, 3);
    AlarmType[] alarmTypes = AlarmType.values(); // INOUT, RESERVE, PAYMENT

    int totalAlarms = 120;
    int alarmsPerType = totalAlarms / alarmTypes.length; // 40개씩
    int half = alarmsPerType / 2; // READ 20, UNREAD 20

    LocalDateTime now = LocalDateTime.now();
    int intervalMinutes = 60;

    for (AlarmType type : alarmTypes) {
      for (int i = 0; i < alarmsPerType; i++) {
        boolean isRead = i < half;

        alarms.add(AlarmEntity.builder()
            .memberEntity(selectedMembers.get(i % selectedMembers.size()))
            .content("[" + type + "] 알림 내용 " + (i + 1))
            .alarmType(type)
            .isRead(isRead ? IsRead.READ : IsRead.UNREAD)
            .createdAt(now.minusMinutes(intervalMinutes * (i + 1)))
            .readAt(isRead ? now.minusMinutes(intervalMinutes * (i + 1) - 5) : null)
            .build()
        );
      }
    }

    alarmRepository.saveAll(alarms);

  }

}
