package org.example.honorsparkingbe.util.converter.dto;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.ParkingFeeRuleDTO;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.springframework.stereotype.Component;

@Component
public class ParkingZoneDTOConverter {

  /**
   * ParkingZoneEntitiy를 ParkingZoneDTO로 바꿔주는 메서드
   *
   * @param parkingZoneEntity
   * @param isFavorite         즐겨찾기 여부 boolean
   * @param parkingFeeRuleList 요금 규칙 배열 List
   * @return ParkingZoneDTO
   */
  public ParkingZoneDTO toDTO(ParkingZoneEntity parkingZoneEntity,
      boolean isFavorite,
      List<ParkingFeeRuleDTO> parkingFeeRuleList
  ) {
    return ParkingZoneDTO.builder()
        .isFavorite(isFavorite)
        .latitude(parkingZoneEntity.getLatitude())
        .longitude(parkingZoneEntity.getLongitude())
        .zoneName(parkingZoneEntity.getZoneName())
        .cityName(parkingZoneEntity.getCityEntity().getName())
        .districtName(parkingZoneEntity.getDistrictEntity().getName())
        .eupMyeonDongName(parkingZoneEntity.getEupMyeonDongEntity().getName())
        .electricCarSpaceCount(parkingZoneEntity.getElectricCarSpaceCount())
        .size(parkingZoneEntity.getSize())
        .floor(parkingZoneEntity.getFloors())
        .maxCost(parkingZoneEntity.getMaxCost())
        .parkingFeeRules(parkingFeeRuleList)
        .build();
  }
}
