//package org.example.honorsparkingbe.dummy.createData;
//
//import jakarta.annotation.PostConstruct;
//import java.time.LocalDateTime;
//import java.util.List;
//import lombok.RequiredArgsConstructor;
//import org.example.honorsparkingbe.domain.entity.CarEntity;
//import org.example.honorsparkingbe.domain.entity.MemberEntity;
//import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
//import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
//import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
//import org.example.honorsparkingbe.domain.enums.CarType;
//import org.example.honorsparkingbe.domain.enums.LoginPlatform;
//import org.example.honorsparkingbe.domain.enums.MemberRole;
//import org.example.honorsparkingbe.domain.enums.PaymentType;
//import org.example.honorsparkingbe.repository.internal.CarRepository;
//import org.example.honorsparkingbe.repository.internal.MemberRepository;
//import org.example.honorsparkingbe.repository.internal.ParkingFeeRuleRepository;
//import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
//import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@DependsOn("createParkingZoneList")  // ParkingZoneList가 먼저 생성되도록 설정
//public class CreateDummyDataForCurrentInfo {
//
//  private final CarRepository carRepository;
//  private final MemberRepository memberRepository;
//  private final ParkingZoneRepository parkingZoneRepository;
//  private final ParkingHistoryRepository parkingHistoryRepository;
//  private final ParkingFeeRuleRepository parkingFeeRuleRepository;
//  private final BCryptPasswordEncoder passwordEncoder;
//
//  @PostConstruct
//  @Transactional
//  public void insertDummyData() {
//    // 이미 데이터가 존재하는지 확인
//    if (memberRepository.count() > 0) {
//      System.out.println("Dummy data already exists. Skipping insertion.");
//      return; // 데이터가 있으면 삽입하지 않음
//    }
//
//    System.out.println("Inserting dummy data for the first time...");
//
//    // Car 데이터 추가
//    CarEntity car1 = CarEntity.builder().carNumber("12가3456").carType(CarType.COMPACT)
//        .isElectric(false).build();
//    CarEntity car2 = CarEntity.builder().carNumber("12나3456").carType(CarType.MIDSIZE)
//        .isElectric(true).build();
//    CarEntity car3 = CarEntity.builder().carNumber("12다3456").carType(CarType.FULLSIZE)
//        .isElectric(false).build();
//    carRepository.saveAll(List.of(car1, car2, car3));
//
//    // Member 데이터 추가 (BCrypt 적용)
//    MemberEntity member1 = MemberEntity.builder()
//        .authId("1111").birthday("01-01").birthdayYear(1990).email("user@example.com")
//        .loginPlatform(LoginPlatform.NORMAL).password(passwordEncoder.encode("1111"))
//        .phoneNumber("01012341234").role(MemberRole.ROLE_USER).userName("name1").carEntity(car1)
//        .build();
//    MemberEntity member2 = MemberEntity.builder()
//        .authId("2222").birthday("01-01").birthdayYear(1990).email("user@example.com")
//        .loginPlatform(LoginPlatform.NORMAL).password(passwordEncoder.encode("2222"))
//        .phoneNumber("01012341234").role(MemberRole.ROLE_USER).userName("name2").carEntity(car2)
//        .build();
//    MemberEntity member3 = MemberEntity.builder()
//        .authId("3333").birthday("01-01").birthdayYear(1990).email("user@example.com")
//        .loginPlatform(LoginPlatform.NORMAL).password(passwordEncoder.encode("3333"))
//        .phoneNumber("01012341234").role(MemberRole.ROLE_USER).userName("name3").carEntity(car3)
//        .build();
//    memberRepository.saveAll(List.of(member1, member2, member3));
//
//    // ParkingZone ID를 사용하여 엔티티 조회
//    ParkingZoneEntity parkingZone1 = parkingZoneRepository.findById(1L)
//        .orElseThrow(() -> new RuntimeException("ParkingZone ID 1 not found"));
//    ParkingZoneEntity parkingZone2 = parkingZoneRepository.findById(2L)
//        .orElseThrow(() -> new RuntimeException("ParkingZone ID 2 not found"));
//    ParkingZoneEntity parkingZone3 = parkingZoneRepository.findById(3L)
//        .orElseThrow(() -> new RuntimeException("ParkingZone ID 3 not found"));
//
//    // ParkingHistory 데이터 추가
//    ParkingHistoryEntity history1 = ParkingHistoryEntity.builder().id(1L)
//        .carEntity(car1).memberEntity(member1).parkingZoneEntity(parkingZone1)
//        .entranceTime(LocalDateTime.of(2025, 2, 10, 8, 30))
//        .exitTime(LocalDateTime.of(2025, 2, 10, 10, 30)).paymentType(PaymentType.ON_SITE).build();
//    ParkingHistoryEntity history2 = ParkingHistoryEntity.builder().id(2L)
//        .carEntity(car2).memberEntity(member2).parkingZoneEntity(parkingZone1)
//        .entranceTime(LocalDateTime.of(2025, 2, 10, 9, 15))
//        .exitTime(LocalDateTime.of(2025, 2, 10, 11, 00)).paymentType(PaymentType.KIOSK).build();
//    ParkingHistoryEntity history3 = ParkingHistoryEntity.builder().id(3L)
//        .carEntity(car2).memberEntity(member2).parkingZoneEntity(parkingZone2)
//        .entranceTime(LocalDateTime.of(2025, 2, 10, 10, 0)).exitTime(null)
//        .paymentType(PaymentType.NONE).build();
//
//    parkingHistoryRepository.saveAll(List.of(history1, history2, history3));
//
//    // ParkingFeeRule 데이터 추가 (중복 체크)
//    if (parkingFeeRuleRepository.count() == 0) {
//      List<ParkingFeeRuleEntity> rules = List.of(
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.COMPACT).costPerTimeSlot(500).costTimeSlot(30)
//              .endTime(30).ruleName("First").startTime(1).parkingZoneEntity(parkingZone1)
//              .build(),
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.COMPACT).costPerTimeSlot(200).costTimeSlot(10)
//              .endTime(120).ruleName("Second").startTime(31).parkingZoneEntity(parkingZone1)
//              .build(),
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.COMPACT).costPerTimeSlot(300).costTimeSlot(10)
//              .endTime(Integer.MAX_VALUE).ruleName("Third").startTime(121)
//              .parkingZoneEntity(parkingZone1)
//              .build(),
//
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.MIDSIZE).costPerTimeSlot(600).costTimeSlot(30)
//              .endTime(30).ruleName("First").startTime(1).parkingZoneEntity(parkingZone2)
//              .build(),
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.MIDSIZE).costPerTimeSlot(250).costTimeSlot(10)
//              .endTime(120).ruleName("Second").startTime(31).parkingZoneEntity(parkingZone2)
//              .build(),
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.MIDSIZE).costPerTimeSlot(350).costTimeSlot(10)
//              .endTime(Integer.MAX_VALUE).ruleName("Third").startTime(121)
//              .parkingZoneEntity(parkingZone2)
//              .build(),
//
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.FULLSIZE).costPerTimeSlot(400).costTimeSlot(30)
//              .endTime(30).ruleName("First").startTime(1).parkingZoneEntity(parkingZone3)
//              .build(),
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.FULLSIZE).costPerTimeSlot(150).costTimeSlot(10)
//              .endTime(120).ruleName("Second").startTime(31).parkingZoneEntity(parkingZone3)
//              .build(),
//          ParkingFeeRuleEntity.builder()
//              .carType(CarType.FULLSIZE).costPerTimeSlot(200).costTimeSlot(10)
//              .endTime(Integer.MAX_VALUE).ruleName("Third").startTime(121)
//              .parkingZoneEntity(parkingZone3)
//              .build()
//      );
//
//      parkingFeeRuleRepository.saveAll(rules);
//    }
//  }
//}
