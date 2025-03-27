package org.example.honorsparkingbe.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.repository.internal.CityRepository;
import org.example.honorsparkingbe.repository.internal.DistrictRepository;
import org.example.honorsparkingbe.repository.internal.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.internal.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
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
public class ParkingFeeRuleRepositoryTest {

  @Autowired
  private ParkingFeeRuleRepository parkingFeeRuleRepository;
  @Autowired
  private ParkingZoneRepository parkingZoneRepository; // Optional: If you want to set up a real parkingZone
  @Autowired
  private CityRepository cityRepository;
  @Autowired
  private DistrictRepository districtRepository;
  @Autowired
  private EupMyeonDongRepository eupMyeonDongRepository;

  private CityEntity seoul;
  private CityEntity busan;
  private CityEntity daejeon;

  private DistrictEntity sampleDistrict;
  private EupMyeonDongEntity sampleEupMyeonDong;

  private ParkingZoneEntity seoulParking;
  private ParkingZoneEntity busanParking;
  private ParkingZoneEntity daejeonParking;

  private ParkingFeeRuleEntity feeRule1;
  private ParkingFeeRuleEntity feeRule2;
  private ParkingFeeRuleEntity feeRule3;

  @BeforeEach
  void setUp() {
    seoul = cityRepository.save(CityEntity.builder().name("서울").build());
    busan = cityRepository.save(CityEntity.builder().name("부산").build());
    daejeon = cityRepository.save(CityEntity.builder().name("대전").build());

    // 구역 엔티티 저장
    sampleDistrict = districtRepository.save(DistrictEntity.builder().name("중구").build());
    sampleEupMyeonDong = eupMyeonDongRepository.save(
        EupMyeonDongEntity.builder().name("데브몬동").build());

    seoulParking = parkingZoneRepository.save(
        ParkingZoneEntity.builder()
            .cityEntity(seoul)
            .districtEntity(sampleDistrict)
            .eupMyeonDongEntity(sampleEupMyeonDong)
            .build()
    );

    busanParking = parkingZoneRepository.save(
        ParkingZoneEntity.builder()
            .cityEntity(busan)
            .districtEntity(sampleDistrict)
            .eupMyeonDongEntity(sampleEupMyeonDong)
            .build()
    );
    daejeonParking = parkingZoneRepository.save(
        ParkingZoneEntity.builder()
            .cityEntity(daejeon)
            .districtEntity(sampleDistrict)
            .eupMyeonDongEntity(sampleEupMyeonDong)
            .build()
    );

    feeRule1 = parkingFeeRuleRepository.save(
        ParkingFeeRuleEntity.builder()
            .ruleName("seoulParking Rule 1")
            .parkingZoneEntity(seoulParking)
            .carType(CarType.LIGHT_CAR)  // 예시로 차 타입 설정
            .startTime(900)  // 9:00 AM
            .endTime(1200)   // 12:00 PM
            .costPerTimeSlot(5000)  // 5000원
            .costTimeSlot(60)  // 1시간
            .build()
    );
    feeRule2 = parkingFeeRuleRepository.save(
        ParkingFeeRuleEntity.builder()
            .ruleName("seoulParking Rule 2")
            .parkingZoneEntity(seoulParking)
            .carType(CarType.LIGHT_CAR)
            .startTime(1200)  // 12:00 PM
            .endTime(1500)   // 3:00 PM
            .costPerTimeSlot(6000)
            .costTimeSlot(60)
            .build()
    );
    feeRule3 = parkingFeeRuleRepository.save(
        ParkingFeeRuleEntity.builder()
            .ruleName("busanParking Rule 3")
            .parkingZoneEntity(busanParking)
            .carType(CarType.LIGHT_CAR)
            .startTime(800)
            .endTime(1100)
            .costPerTimeSlot(4000)
            .costTimeSlot(60)
            .build()
    );
  }

  @Test
  void testFindAllByParkingZoneEntity_Id() {
    // When: ParkingZoneId로 요금 규칙을 찾음
    List<ParkingFeeRuleEntity> rules = parkingFeeRuleRepository.findAllByParkingZoneEntity_Id(
        seoulParking.getId());

    // Then: 두 개의 요금 규칙이 반환되어야 함
    assertNotNull(rules);
    assertEquals(2, rules.size());
    assertThat(rules)
        .extracting(ParkingFeeRuleEntity::getRuleName)
        .containsExactlyInAnyOrder("seoulParking Rule 1", "seoulParking Rule 2");
  }

  @Test
  void testFindAllByParkingZoneEntity_Id_NoResults() {
    // When: 존재하지 않는 ParkingZoneId로 검색
    List<ParkingFeeRuleEntity> rules = parkingFeeRuleRepository.findAllByParkingZoneEntity_Id(
        999L); // 존재하지 않는 ID

    // Then: 빈 리스트가 반환되어야 함
    assertNotNull(rules);
    assertTrue(rules.isEmpty());
  }
}
