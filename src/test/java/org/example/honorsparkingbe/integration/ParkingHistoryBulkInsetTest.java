//package org.example.honorsparkingbe.integration;
//
//import static org.junit.Assert.assertEquals;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import org.example.honorsparkingbe.domain.entity.CarEntity;
//import org.example.honorsparkingbe.domain.entity.CityEntity;
//import org.example.honorsparkingbe.domain.entity.DistrictEntity;
//import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
//import org.example.honorsparkingbe.domain.entity.MemberEntity;
//import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
//import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
//import org.example.honorsparkingbe.domain.enums.LoginPlatform;
//import org.example.honorsparkingbe.domain.enums.MemberRole;
//import org.example.honorsparkingbe.domain.enums.PaymentType;
//import org.example.honorsparkingbe.repository.internal.CarRepository;
//import org.example.honorsparkingbe.repository.internal.CityRepository;
//import org.example.honorsparkingbe.repository.internal.DistrictRepository;
//import org.example.honorsparkingbe.repository.internal.EupMyeonDongRepository;
//import org.example.honorsparkingbe.repository.internal.MemberRepository;
//import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
//import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class ParkingHistoryBulkInsetTest extends InitIntegrationTest {
//
//  @Autowired
//  private ParkingHistoryRepository parkingHistoryRepository;
//
//  @Autowired
//  private MemberRepository memberRepository;
//  @Autowired
//  private CarRepository carRepository;
//  @Autowired
//  private CityRepository cityRepository;
//  @Autowired
//  private DistrictRepository districtRepository;
//  @Autowired
//  private EupMyeonDongRepository eupMyeonDongRepository;
//  @Autowired
//  private ParkingZoneRepository parkingZoneRepository;
//
//  private CarEntity testCar1;
//  private CityEntity seoul;
//  private DistrictEntity sampleDistrict;
//  private EupMyeonDongEntity sampleEupMyeonDong;
//  private ParkingZoneEntity testParkingZone;
//  private MemberEntity testMember;
//
//  @BeforeEach
//  void setUp2() {
//
//    // not null을 위한 세팅
//    testCar1 = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber1").build());
//
//    // 도시 엔티티 저장
//    seoul = cityRepository.save(CityEntity.builder().name("서울").build());
//
//    // 구역 엔티티 저장
//    sampleDistrict = districtRepository.save(DistrictEntity.builder().name("중구").build());
//    sampleEupMyeonDong = eupMyeonDongRepository.save(
//        EupMyeonDongEntity.builder().name("데브몬동").build());
//
//    testParkingZone = parkingZoneRepository.save(
//        ParkingZoneEntity.builder()
//            .cityEntity(seoul)
//            .districtEntity(sampleDistrict)
//            .eupMyeonDongEntity(sampleEupMyeonDong)
//            .build()
//    );
//
//    testMember = memberRepository.save(
//        MemberEntity.builder()
//            .carEntity(testCar1)
//            .userName("12bookMarkUser")
//            .authId("authId1")
//            .password("password2")
//            .phoneNumber("01087654321")
//            .email("user1@example.com")
//            .birthdayYear(1985)
//            .birthday("0202")
//            .loginPlatform(LoginPlatform.GOOGLE)
//            .role(MemberRole.ROLE_USER)
//            .build()
//    );
//  }
//
//  @Test
//  void bulkInsertPerformanceTest() {
//    // 1000개의 ParkingHistoryEntity 생성
//    Long ENTITY_SIZE = 1000L;
//    List<ParkingHistoryEntity> parkingHistoryEntities = new ArrayList<>();
//    for (Long i = 0L; i < ENTITY_SIZE; i++) {
//      ParkingHistoryEntity entity = ParkingHistoryEntity.builder()
//          .id(i + 1)
//          .carEntity(testCar1)
//          .memberEntity(testMember)
//          .parkingZoneEntity(testParkingZone)
//          .entranceTime(LocalDateTime.now())
//          .paymentType(PaymentType.NONE)
//          .build();
//      // 필요한 필드 추가 설정
//      parkingHistoryEntities.add(entity);
//    }
//
//    // 실행 시간 측정 시작
//    long startTime = System.nanoTime();
//
//    // Bulk Insert 실행
//    parkingHistoryRepository.bulkInsertAndUpdate(parkingHistoryEntities);
//
//    // 실행 시간 측정 종료
//    long endTime = System.nanoTime();
//
//    // 실행 시간 출력 (밀리초 변환)
//    long durationMs = (endTime - startTime) / 1_000_000;
//    System.out.println("Bulk Insert" + ENTITY_SIZE + "개 실행 시간: " + durationMs + " ms");
//
//    // 저장된 데이터 개수 검증
//    assertEquals(ENTITY_SIZE.intValue(), parkingHistoryRepository.count());
//  }
//
//}
