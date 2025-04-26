package org.example.honorsparkingbe.performanceTest.createData;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 1) 가상의 주차장을 30개 만들어야함 값은 랜덤하게 들어가는 것으로 합니다. 또한 이에 맞는 요금규칙도 만들어야합니다. 2) 3) 유저 3명을 만듭니다.
 */
@Component
@Profile("performanceTest")
public class CreateParkingZonesInPerformanceTest extends PerformanceCheckInit {

  @PostConstruct
  @Transactional
  public void insertParkingZoneDataForPerformanceTest() {
    List<ParkingZoneEntity> newParkingZones = getSampleParkingZones(sampleParkingZoneNum);
    parkingZoneRepository.saveAll(newParkingZones);

    List<ParkingZoneEntity> savedParkingZones = parkingZoneRepository.findAll();
    savedParkingZones.forEach(targetParkingZone -> {
      ParkingFeeRuleEntity newFeeRule = ParkingFeeRuleEntity.builder()
          .carType(CarType.FULLSIZE).costPerTimeSlot(200).costTimeSlot(10)
          .endTime(Integer.MAX_VALUE).ruleName("First").startTime(1)
          .parkingZoneEntity(targetParkingZone)
          .build();

      parkingFeeRuleRepository.save(newFeeRule);
    });
  }


  private List<ParkingZoneEntity> getSampleParkingZones(Integer size) {
    List<ParkingZoneEntity> parkingZones = new ArrayList<>();

    CityEntity seoul = cityRepository.save(CityEntity.builder().name("Seoul").build());
    DistrictEntity gangnam = districtRepository.save(DistrictEntity.builder().name("강남구").build());
    EupMyeonDongEntity doma = eupMyeonDongRepository.save(
        EupMyeonDongEntity.builder().name("도마동").build());

    for (int i = 0; i < size; i++) {
      double randomLatitude = ThreadLocalRandom.current().nextDouble(33.0, 38.5);
      double randomLongitude = ThreadLocalRandom.current().nextDouble(124.0, 132.0);

      ParkingZoneEntity newParkingZone = ParkingZoneEntity.builder()
          .zoneName("Performance Parking Lot" + i)
          .size(10).maxCost(100000)
          .latitude(randomLatitude).longitude(randomLongitude)
          .cityEntity(seoul).districtEntity(gangnam).eupMyeonDongEntity(doma)
          .thumbnailUrl(
              "https://res.cloudinary.com/dhabktrg9/image/upload/v1739866539/seblxkuswovn9w1mu5em.png")
          .build();
      parkingZones.add(newParkingZone);
    }

    return parkingZones;
  }
}
