//package org.example.honorsparkingbe.integration;
//
//import static org.junit.Assert.assertEquals;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import org.example.honorsparkingbe.domain.entity.CarEntity;
//import org.example.honorsparkingbe.domain.entity.MemberEntity;
//import org.example.honorsparkingbe.domain.entity.PayEntity;
//import org.example.honorsparkingbe.domain.enums.LoginPlatform;
//import org.example.honorsparkingbe.domain.enums.MemberRole;
//import org.example.honorsparkingbe.repository.internal.CarRepository;
//import org.example.honorsparkingbe.repository.internal.MemberRepository;
//import org.example.honorsparkingbe.repository.internal.PayRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class PayEntityBulkInsertTest extends InitIntegrationTest {
//
//  @Autowired
//  private PayRepository payRepository;
//  @Autowired
//  private CarRepository carRepository;
//
//  @Autowired
//  private MemberRepository memberRepository;
//
//  private MemberEntity testMember;
//  private CarEntity testCar;
//
//  @BeforeEach
//  void setUp2() {
//    testCar = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber1").build());
//    // 테스트에 필요한 MemberEntity 저장
//
//    testMember = memberRepository.save(
//        MemberEntity.builder()
//            .carEntity(testCar)
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
//    // 1000개의 PayEntity 생성
//    Long ENTITY_SIZE = 1000L;
//    List<PayEntity> payEntities = new ArrayList<>();
//
//    for (Long i = 0L; i < ENTITY_SIZE; i++) {
//      PayEntity entity = PayEntity.builder()
//          .id(i + 1)
//          .amount(10000)
//          .paidAt(LocalDateTime.now())
//          .memberEntity(testMember)
//          .build();
//      payEntities.add(entity);
//    }
//
//    // 실행 시간 측정 시작
//    long startTime = System.nanoTime();
//
//    // Bulk Insert 실행
//    List<PayEntity> result = payRepository.bulkInsertAndUpdate(payEntities);
//
//    // 실행 시간 측정 종료
//    long endTime = System.nanoTime();
//
//    // 실행 시간 출력 (밀리초 변환)
//    long durationMs = (endTime - startTime) / 1_000_000;
//    System.out.println("Bulk Insert " + ENTITY_SIZE + "개 실행 시간: " + durationMs + " ms");
//
//    // 저장된 데이터 개수 검증
//    assertEquals(ENTITY_SIZE.intValue(), payRepository.count());
//  }
//
//}
