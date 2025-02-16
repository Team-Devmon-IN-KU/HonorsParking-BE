package org.example.honorsparkingbe.util.converter.dto;

import java.util.List;
import java.util.stream.Collectors;
import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
import org.example.honorsparkingbe.dto.ParkingFeeRuleDTO;
import org.springframework.stereotype.Component;

@Component
public class ParkingFeeRuleDTOConverter {

  /**
   * ParkingFeeRuleEntity를 DTO로 변환합니다.
   *
   * @param parkingFeeRuleEntity
   * @return ParkingFeeRuleDTO
   */
  public ParkingFeeRuleDTO toDto(ParkingFeeRuleEntity parkingFeeRuleEntity) {

    return ParkingFeeRuleDTO.builder()
        .ruleName(parkingFeeRuleEntity.getRuleName())
        .startTime(parkingFeeRuleEntity.getStartTime())
        .endTime(parkingFeeRuleEntity.getEndTime())
        .costPerTimeSlot(parkingFeeRuleEntity.getCostPerTimeSlot())
        .costTimeSlot(parkingFeeRuleEntity.getCostTimeSlot())
        .build();
  }

  /**
   * 주차 요금 규칙 엔티티 목록을 DTO 목록으로 변환합니다.
   *
   * @param parkingFeeRuleEntities 변환할 주차 요금 규칙 엔티티 목록
   * @return 변환된 주차 요금 규칙 DTO 목록
   */
  public List<ParkingFeeRuleDTO> toDtoList(List<ParkingFeeRuleEntity> parkingFeeRuleEntities) {
    return parkingFeeRuleEntities.stream()
        .map(this::toDto) // 각 엔티티를 DTO로 변환
        .collect(Collectors.toList()); // 결과를 리스트로 수집
  }
}
