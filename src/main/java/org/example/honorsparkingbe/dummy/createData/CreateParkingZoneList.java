package org.example.honorsparkingbe.dummy.createData;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.repository.CarRepository;
import org.example.honorsparkingbe.repository.CityRepository;
import org.example.honorsparkingbe.repository.DistrictRepository;
import org.example.honorsparkingbe.repository.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.MemberRepository;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateParkingZoneList {

  private final CityRepository cityRepository;
  private final DistrictRepository districtRepository;
  private final EupMyeonDongRepository eupMyeonDongRepository;
  private final ParkingZoneRepository parkingZoneRepository;
  private final ParkingHistoryRepository parkingHistoryRepository;
  private final CarRepository carRepository;
  private final MemberRepository memberRepository;

  @PostConstruct
  @Transactional
  public void insertParkingZoneDummyData() {
    // City 데이터 추가
    CityEntity YonginCity = cityRepository.findByName("용인시")
        .orElse(CityEntity.builder().name("용인시").build());

    cityRepository.saveAll(List.of(YonginCity));

    // District 데이터 추가 (중복 체크)
    DistrictEntity CheinGu = districtRepository.findByName("처인구")
        .orElse(DistrictEntity.builder().name("처인구").build());

    districtRepository.saveAll(List.of(CheinGu));

    // EupMyeonDong 데이터 추가 (중복 체크)
    EupMyeonDongEntity BaekyapMyeon = eupMyeonDongRepository.findByName("백암면")
        .orElse(EupMyeonDongEntity.builder().name("백암면").build());

    eupMyeonDongRepository.saveAll(List.of(BaekyapMyeon));

    // ParkingZone 데이터 추가
    ParkingZoneEntity parkingZone1 = ParkingZoneEntity.builder()
        .zoneName("A타워")
        .size(10)
        .latitude(37.4979)
        .longitude(127.0276)
        .cityEntity(YonginCity)
        .districtEntity(CheinGu)
        .eupMyeonDongEntity(BaekyapMyeon)
        .build();

    ParkingZoneEntity parkingZone2 = ParkingZoneEntity.builder()
        .zoneName("B타워")
        .size(5)
        .latitude(35.1710)
        .longitude(129.1213)
        .cityEntity(YonginCity)
        .districtEntity(CheinGu)
        .eupMyeonDongEntity(BaekyapMyeon)
        .build();

    ParkingZoneEntity parkingZone3 = ParkingZoneEntity.builder()
        .zoneName("용인 공영 주차장")
        .size(3)
        .latitude(36.3510)
        .longitude(127.3845)
        .cityEntity(YonginCity)
        .districtEntity(CheinGu)
        .eupMyeonDongEntity(BaekyapMyeon)
        .build();

    // 중복 체크 후 주차장 저장
    if (!parkingZoneRepository.existsByZoneName(parkingZone1.getZoneName())) {
      parkingZoneRepository.save(parkingZone1);
    }
    if (!parkingZoneRepository.existsByZoneName(parkingZone2.getZoneName())) {
      parkingZoneRepository.save(parkingZone2);
    }
    if (!parkingZoneRepository.existsByZoneName(parkingZone3.getZoneName())) {
      parkingZoneRepository.save(parkingZone3);
    }
  }
}
