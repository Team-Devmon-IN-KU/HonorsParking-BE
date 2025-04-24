package org.example.honorsparkingbe.performanceTest.createData;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.enums.PaymentType;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("performanceTest")
@DependsOn({"createParkingZonesInPerformanceTest", "createUsersInPerformanceTest"})
public class CreateParkingHistoriesInPerformanceTest extends PerformanceCheckInit {

  @PostConstruct
  @Transactional
  /**
   * 유저 a,b,c기반의 parkingHistory를 랜덤하게 90개 만듭니다.
   * 	이때 연도는 2023년 1월 1일부터 2025년 12월 31일까지 랜덤입니다.
   *     모든 간격의 데이터가 균등하게 있어야합니다.
   *     이때 유저상태에 따른 주차현재 상태는 아래와 같습니다.
   *     유저 a -> 주차 ❌
   * 	유저 b -> 주차 ✅ 2시간 정도 경과
   * 	유저 c -> 주차 ✅ 30분 초과 x 위의 주차장과 달라야함
   */
  public void insertParkingHistoryFormPerformanceTest() {

    List<MemberEntity> targetMembers = memberRepository.findAll();
    List<ParkingZoneEntity> parkingZoneEntities = parkingZoneRepository.findAll();
    List<ParkingHistoryEntity> parkingHistories = new ArrayList<>();
    Random random = new Random();
    long id = 1L;

    // 기준 날짜
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2025, 12, 31);
    long totalDays = ChronoUnit.DAYS.between(startDate, endDate);

    int totalHistories = 90;

    // 유저 리스트 분리
    MemberEntity userA = targetMembers.get(0); // a
    MemberEntity userB = targetMembers.get(1); // b
    MemberEntity userC = targetMembers.get(2); // c

    // 90개 기록 생성 (유저 a, b, c 균등하게 30개씩)
    for (int i = 0; i < totalHistories; i++) {
      MemberEntity member;
      if (i < 30) {
        member = userA;
      } else if (i < 60) {
        member = userB;
      } else {
        member = userC;
      }

      CarEntity car = member.getCarEntity();
      ParkingZoneEntity zone = parkingZoneEntities.get(random.nextInt(parkingZoneEntities.size()));

      long baseOffset = (i * totalDays) / totalHistories;
      long offset = baseOffset + random.nextInt((int) (totalDays / totalHistories));
      LocalDate randomDate = startDate.plusDays(offset);

      LocalDateTime entranceTime = randomDate.atTime(8 + random.nextInt(4), 0);
      LocalDateTime exitTime = entranceTime.plusHours(1 + random.nextInt(3));

      ParkingHistoryEntity entity = ParkingHistoryEntity.builder()
          .id(id++)
          .carEntity(car)
          .memberEntity(member)
          .parkingZoneEntity(zone)
          .entranceTime(entranceTime)
          .exitTime(exitTime)
          .paymentType(PaymentType.ON_SITE)
          .build();

      parkingHistories.add(entity);
    }

// 유저 b: 현재 주차 중 (2시간 경과)
    ParkingZoneEntity zoneB = parkingZoneEntities.get(0);
    parkingHistories.add(ParkingHistoryEntity.builder()
        .id(id++)
        .carEntity(userB.getCarEntity())
        .memberEntity(userB)
        .parkingZoneEntity(zoneB)
        .entranceTime(LocalDateTime.now().minusHours(2))
        .exitTime(null)
        .paymentType(PaymentType.ON_SITE)
        .build()
    );

// 유저 c: 현재 주차 중 (30분 이내), 다른 주차장
    ParkingZoneEntity zoneC = parkingZoneEntities.get(1);
    if (zoneC.equals(zoneB)) {
      zoneC = parkingZoneEntities.get(2); // 중복 방지
    }
    parkingHistories.add(ParkingHistoryEntity.builder()
        .id(id++)
        .carEntity(userC.getCarEntity())
        .memberEntity(userC)
        .parkingZoneEntity(zoneC)
        .entranceTime(LocalDateTime.now().minusMinutes(25))
        .exitTime(null)
        .paymentType(PaymentType.ON_SITE)
        .build()
    );

// 저장
    parkingHistoryRepository.saveAll(parkingHistories);
  }
}
