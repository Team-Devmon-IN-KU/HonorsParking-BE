package org.example.honorsparkingbe.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.repository.CityRepository;
import org.example.honorsparkingbe.repository.DistrictRepository;
import org.example.honorsparkingbe.repository.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@TestPropertySource(locations = "classpath:config/application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ParkingZoneRepositoryTest {

  @Autowired
  private ParkingZoneRepository parkingZoneRepository;

  @Autowired
  private CityRepository cityRepository;

  @Autowired
  DistrictRepository districtRepository;

  @Autowired
  EupMyeonDongRepository eupMyeonDongRepository;

  private ParkingZoneEntity seoulParking;
  private ParkingZoneEntity busanParking;
  private ParkingZoneEntity daejeonParking;


  @BeforeEach
  void setUp() {
    CityEntity seoul = new CityEntity();
    seoul.setName("서울");
    cityRepository.save(seoul);

    CityEntity busan = new CityEntity();
    busan.setName("부산");
    cityRepository.save(busan);

    CityEntity daejeon = new CityEntity();
    daejeon.setName("대전");
    cityRepository.save(daejeon);

    DistrictEntity sampleDistrict = new DistrictEntity();
    sampleDistrict.setName("중구");
    districtRepository.save(sampleDistrict);

    EupMyeonDongEntity sampleEupMyeonDong = new EupMyeonDongEntity();
    sampleEupMyeonDong.setName("데브몬동");
    eupMyeonDongRepository.save(sampleEupMyeonDong);

    // 서울 주차장
    seoulParking = new ParkingZoneEntity();
    seoulParking.setLatitude(37.5665);  // 서울 위도
    seoulParking.setLongitude(126.9780); // 서울 경도
    seoulParking.setZoneName("서울 주차장");
    seoulParking.setSize(100);
    seoulParking.setMaxCost(12000);
    seoulParking.setAddress("서울특별시");

    seoulParking.setCityEntity(seoul);
    seoulParking.setDistrictEntity(sampleDistrict);
    seoulParking.setEupMyeonDongEntity(sampleEupMyeonDong);

    // 부산 주차장
    busanParking = new ParkingZoneEntity();
    busanParking.setLatitude(35.1796); // 부산 위도
    busanParking.setLongitude(129.0756); // 부산 경도
    busanParking.setZoneName("부산 주차장");
    busanParking.setSize(80);
    busanParking.setMaxCost(10000);
    busanParking.setAddress("부산광역시");

    busanParking.setCityEntity(busan);
    busanParking.setDistrictEntity(sampleDistrict);
    busanParking.setEupMyeonDongEntity(sampleEupMyeonDong);

    // 대전 주차장
    daejeonParking = new ParkingZoneEntity();
    daejeonParking.setLatitude(36.3504); // 대전 위도
    daejeonParking.setLongitude(127.3845); // 대전 경도
    daejeonParking.setZoneName("대전 주차장");
    daejeonParking.setSize(50);
    daejeonParking.setMaxCost(8000);
    daejeonParking.setAddress("대전광역시");

    daejeonParking.setCityEntity(daejeon);
    daejeonParking.setDistrictEntity(sampleDistrict);
    daejeonParking.setEupMyeonDongEntity(sampleEupMyeonDong);

    parkingZoneRepository.saveAll(List.of(seoulParking, busanParking, daejeonParking));
  }

  @Test
  @DisplayName("위경도 기준 가까운 거리순 정렬 ID 정렬확인")
  void testFindClosestParkingZonesIds() {
    double latitude = 37.5665; // 서울 기준
    double longitude = 126.9780;
    int limit = 3;

    List<Long> result = parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        latitude,
        longitude, limit, 0, new ArrayList<>(Arrays.asList(0L)));

    assertThat(result).hasSize(3);
    assertThat(result)
        .containsExactly(
            seoulParking.getId(),
            daejeonParking.getId(),
            busanParking.getId()
        );
  }

  @Test
  @DisplayName("위경도 기준 가까운 거리순 정렬 - 특정 ID 1개 제외")
  void testFindClosestParkingZonesIdsWithExclusion1() {
    double latitude = 37.5665; // 서울 기준
    double longitude = 126.9780;
    int limit = 3;

    List<Long> excludedIds = List.of(seoulParking.getId()); // 서울 주차장 제외

    List<Long> result = parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        latitude,
        longitude, limit, 0, excludedIds);

    assertThat(result).hasSize(2); // 서울 주차장이 제외되었으므로 2개만 남아야 함
    assertThat(result)
        .containsExactly(
            daejeonParking.getId(),
            busanParking.getId()
        ); // 대전 → 부산 순서로 정렬되어야 함
  }

  @Test
  @DisplayName("위경도 기준 가까운 거리순 정렬 - 특정 ID 3개 (모두)제외")
  void testFindClosestParkingZonesIdsWithExclusionAll() {
    double latitude = 37.5665; // 서울 기준
    double longitude = 126.9780;
    int limit = 3;

    List<Long> excludedIds = List.of(seoulParking.getId(), daejeonParking.getId(),
        busanParking.getId()); // 서울 주차장 제외

    List<Long> result = parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        latitude,
        longitude, limit, 0, excludedIds);

    assertThat(result).hasSize(0); // 서울 주차장이 제외되었으므로 2개만 남아야 함
    assertThat(result)
        .containsExactly(
        ); // 빈 배열이어야함
  }

  @Test
  @DisplayName("ID 배열로 주차장 리스트 반환 테스트")
  void testFindAllByIdIn() {
    // 저장된 주차장의 ID 리스트 가져오기
    List<Long> ids = parkingZoneRepository.findAll().stream()
        .map(ParkingZoneEntity::getId)
        .collect(Collectors.toList());

    // ID 목록으로 주차장 조회
    List<ParkingZoneEntity> result = parkingZoneRepository.findAllByIdIn(ids);

    assertThat(result).hasSize(3);  // 3개가 반환되어야 함
    assertThat(result).extracting(ParkingZoneEntity::getZoneName)
        .containsExactlyInAnyOrder("서울 주차장", "부산 주차장", "대전 주차장");
  }

//  @Test
//  @DisplayName("특정 ID 배열을 제외한 주차장 리스트 반환 테스트")
//  void testFindAllByIdNotIn() {
//    // given
//    // 모든 주차장 목록의 ID 가져오기
//    List<Long> allIds = parkingZoneRepository.findAll().stream()
//        .map(ParkingZoneEntity::getId)
//        .collect(Collectors.toList());
//
//    // sample 위경도값 세팅
//    double latitude = 37.5665; // 서울 기준
//    double longitude = 126.9780;
//    long limit = 3;
//
//    // 제외할 ID 목록 (예: 서울 주차장과 부산 주차장을 제외)
//    List<Long> excludeIds = allIds.stream()
//        .filter(id -> id.equals(seoulParking.getId()) || id.equals(busanParking.getId()))
//        .collect(Collectors.toList());
//
//    // when
//    // 제외된 ID를 반영한 결과 가져오기
//    List<ParkingZoneEntity> result = parkingZoneRepository.findClosestParkingZonesWithExclusion(
//        latitude, longitude, limit, 0L, excludeIds);
//
//    // 테스트 검증
//    assertThat(result).hasSize(1);  // 대전 주차장만 남아야 함
//    assertThat(result).extracting(ParkingZoneEntity::getZoneName)
//        .containsExactly("대전 주차장");  // "대전 주차장"이 반환되어야 함
//  }

//  @Test
//  @DisplayName("제외할 주차장 ID 배열이 빈 경우 모든 주차장 반환 테스트")
//  void testFindAllByIdNotIn_EmptyExcludeIds() {
//    // given
//    // 모든 주차장 목록의 ID 가져오기
//    List<Long> allIds = parkingZoneRepository.findAll().stream()
//        .map(ParkingZoneEntity::getId)
//        .collect(Collectors.toList());
//
//    // sample 위경도값 세팅
//    double latitude = 37.5665; // 서울 기준
//    double longitude = 126.9780;
//    long limit = 3;
//
//    // 제외할 ID 목록은 빈 리스트
////        List<Long> excludeIds = new ArrayList<>();  // 빈 배열
//    List<Long> excludeIds = new ArrayList<>(Collections.singletonList(0L));
//    // Service레이어에서 전처리가 이루어지므로 0인 빈배열 주기 ParkingZone은 1 based 넘버링이기 때무에
//
//    // when
//    // 제외할 ID가 없으므로, 모든 주차장을 가져오도록 설정
//    List<ParkingZoneEntity> result = parkingZoneRepository.findClosestParkingZonesWithExclusion(
//        latitude, longitude, limit, 0L, excludeIds);
//
//    // then
//    // 결과가 제한 개수(limit)만큼 반환되어야 함
//    assertThat(result).hasSize(3);  // limit = 3이므로 3개의 주차장이 반환되어야 함
//    assertThat(result).extracting(ParkingZoneEntity::getZoneName)
//        .containsExactly("서울 주차장", "대전 주차장", "부산 주차장");  // 주차장 이름이 포함되어야 함
//  }
}
