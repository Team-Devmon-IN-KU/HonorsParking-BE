package org.example.honorsparkingbe.unit.repository;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.PayEntity;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.domain.enums.PaymentType;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.CityRepository;
import org.example.honorsparkingbe.repository.internal.DistrictRepository;
import org.example.honorsparkingbe.repository.internal.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.PayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ParkingHistoryRepositoryTest {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private ParkingHistoryRepository parkingHistoryRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private CarRepository carRepository;
  @Autowired
  private CityRepository cityRepository;
  @Autowired
  private DistrictRepository districtRepository;
  @Autowired
  private EupMyeonDongRepository eupMyeonDongRepository;
  @Autowired
  private ParkingZoneRepository parkingZoneRepository;
  @Autowired
  private PayRepository payRepository;

  //for not null condition
  private CarEntity testCar1;
  private CarEntity testCar2;
  private CityEntity seoul;
  private DistrictEntity sampleDistrict;
  private EupMyeonDongEntity sampleEupMyeonDong;
  private ParkingZoneEntity testParkingZone;
  private PayEntity testPay1;
  private PayEntity testPay2;
  private MemberEntity testMember1;
  private MemberEntity testMember2;

  //given
  private ParkingHistoryEntity history1;
  private ParkingHistoryEntity history2;
  private ParkingHistoryEntity preSoftDeletedHistory;
  private ParkingHistoryEntity memberHistoryButNotDeletRequest;

  @BeforeEach
  void setUp() {

    // not null을 위한 세팅
    testCar1 = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber1").build());
    testCar2 = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber2").build());

    // 도시 엔티티 저장
    seoul = cityRepository.save(CityEntity.builder().name("서울").build());

    // 구역 엔티티 저장
    sampleDistrict = districtRepository.save(DistrictEntity.builder().name("중구").build());
    sampleEupMyeonDong = eupMyeonDongRepository.save(
        EupMyeonDongEntity.builder().name("데브몬동").build());

    testParkingZone = parkingZoneRepository.save(
        ParkingZoneEntity.builder()
            .cityEntity(seoul)
            .districtEntity(sampleDistrict)
            .eupMyeonDongEntity(sampleEupMyeonDong)
            .build()
    );

    testMember1 = memberRepository.save(
        MemberEntity.builder()
            .carEntity(testCar1)
            .userName("12bookMarkUser")
            .authId("authId1")
            .password("password2")
            .phoneNumber("01087654321")
            .email("user1@example.com")
            .birthdayYear(1985)
            .birthday("0202")
            .loginPlatform(LoginPlatform.GOOGLE)
            .role(MemberRole.ROLE_USER)
            .build()
    );
    testMember2 = memberRepository.save(
        MemberEntity.builder()
            .carEntity(testCar2)
            .userName("12bookMarkUser")
            .authId("authId2")
            .password("password2")
            .phoneNumber("01087654321")
            .email("user1@example.com")
            .birthdayYear(1985)
            .birthday("0202")
            .loginPlatform(LoginPlatform.GOOGLE)
            .role(MemberRole.ROLE_USER)
            .build()
    );
    testPay1 = payRepository.save(
        PayEntity.builder()
            .memberEntity(testMember1)
            .build()
    );
    testPay2 = payRepository.save(
        PayEntity.builder()
            .memberEntity(testMember2)
            .build()
    );

    // Given
    history1 = ParkingHistoryEntity.builder()
        .id(1L)
        .deleteAt(null)
        .entranceTime(LocalDateTime.now())
        .carEntity(testCar1)
        .parkingZoneEntity(testParkingZone)
        .paymentType(PaymentType.NONE)
        .memberEntity(testMember1)
        .payEntity(testPay1)
        .build();
    history2 = ParkingHistoryEntity.builder()
        .id(2L)
        .deleteAt(null)
        .entranceTime(LocalDateTime.now())
        .carEntity(testCar2)
        .parkingZoneEntity(testParkingZone)
        .paymentType(PaymentType.NONE)
        .memberEntity(testMember2)
        .payEntity(testPay2)
        .build();
    preSoftDeletedHistory = ParkingHistoryEntity.builder()
        .id(3L)
        .deleteAt(LocalDateTime.now())
        .entranceTime(LocalDateTime.now())
        .entranceTime(LocalDateTime.now())
        .carEntity(testCar2)
        .parkingZoneEntity(testParkingZone)
        .paymentType(PaymentType.NONE)
        .memberEntity(testMember2)
        .payEntity(testPay2)
        .build();
    memberHistoryButNotDeletRequest = ParkingHistoryEntity.builder()
        .id(4L)
        .deleteAt(LocalDateTime.now())
        .entranceTime(LocalDateTime.now())
        .entranceTime(LocalDateTime.now())
        .carEntity(testCar2)
        .parkingZoneEntity(testParkingZone)
        .paymentType(PaymentType.NONE)
        .memberEntity(testMember2)
        .payEntity(testPay2)
        .build();
    parkingHistoryRepository.saveAll(List.of(history1, history2, preSoftDeletedHistory));

  }

  @Test
  void updateDeleteAtByIds_ShouldUpdateDeleteAt_WhenIdsExist() {
    // When
    LocalDateTime now = LocalDateTime.now();
    parkingHistoryRepository.softDeleteAtByIds(now, List.of(history2.getId()));

    entityManager.flush(); // 즉시 반영
    entityManager.clear(); // 영속성 컨텍스트 초기화

    // Then
    ParkingHistoryEntity nonDeletedHistory1 = parkingHistoryRepository.findById(history1.getId())
        .orElse(null);
    ParkingHistoryEntity deleteHistory = parkingHistoryRepository.findById(history2.getId())
        .orElse(null);

    assertNotNull(nonDeletedHistory1);
    assertNotNull(deleteHistory);
    assertEquals(null, nonDeletedHistory1.getDeleteAt());
    assertNotEquals(preSoftDeletedHistory.getDeleteAt(), nonDeletedHistory1.getDeleteAt());
    assertThat(deleteHistory.getDeleteAt())
        .isCloseTo(now, within(100, ChronoUnit.MILLIS)); // 1/10초까지 허용
  }

  @Test
  void findByIdsAndMember_ShouldReturnHistories_WhenIdsAndMemberMatch() {
    // Given
    Long memberId = testMember1.getId();
    history1.setMemberEntity(testMember1);
    history2.setMemberEntity(testMember1);
    memberHistoryButNotDeletRequest.setMemberEntity(testMember1);

    // Persist updated entities
    parkingHistoryRepository.saveAll(List.of(history1, history2));

    // When
    List<ParkingHistoryEntity> result = parkingHistoryRepository.findByIdsAndMember(
        List.of(history1.getId(), history2.getId()), memberId);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(
        result.stream().allMatch(history -> history.getMemberEntity().getId().equals(memberId)));
  }

  @Test
  void deleteAllByDeleteAtBefore_ShouldDeleteRecords_WhenMatchingRecordsExist() {
    // Given (초기 데이터 저장)
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime pastTime = now.minusDays(2); // 2일 전의 날짜
    LocalDateTime recentTime = now.minusHours(1); // 1시간 전의 날짜

    history1.setDeleteAt(pastTime);
    history2.setDeleteAt(recentTime);
    preSoftDeletedHistory.setDeleteAt(pastTime);
    parkingHistoryRepository.saveAll(List.of(history1, history2, preSoftDeletedHistory));

    ParkingHistoryEntity oldHistory1 = parkingHistoryRepository.findById(history1.getId())
        .orElse(null);
    ParkingHistoryEntity newHistory = parkingHistoryRepository.findById(history2.getId())
        .orElse(null);
    ParkingHistoryEntity oldHistory2 = parkingHistoryRepository.findById(
        preSoftDeletedHistory.getId()).orElse(null);

    // 저장된 데이터 개수 확인
    assertEquals(3, parkingHistoryRepository.count());

    // When (deleteAt이 now 이전인 데이터 삭제)
    LocalDateTime OneDayBefore = now.minusDays(1);
    parkingHistoryRepository.deleteAllByDeleteAtBefore(OneDayBefore);

    // Then (삭제 검증)
    List<ParkingHistoryEntity> remainingHistories = parkingHistoryRepository.findAll();

    assertEquals(1, remainingHistories.size()); // 한 개만 남아 있어야 함
    assertEquals(recentTime, remainingHistories.get(0).getDeleteAt()); // 남은 데이터는 최근 데이터여야 함
  }

}
