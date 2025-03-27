package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.repository.internal.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrentInfoService {

  private final ParkingHistoryRepository parkingHistoryRepository;
  private final ParkingFeeRuleRepository parkingFeeRuleRepository;

  public CurrentInfoService(ParkingHistoryRepository parkingHistoryRepository,
      ParkingFeeRuleRepository parkingFeeRuleRepository) {
    this.parkingHistoryRepository = parkingHistoryRepository;
    this.parkingFeeRuleRepository = parkingFeeRuleRepository;
  }

  public Map<String, Object> getCurrentParkingInfo(Long memberId) {

    // ParkingHistory에서 해당 memberId를 가진 가장 최신의 기록 가져오기
    ParkingHistoryEntity latestHistory = parkingHistoryRepository.findFirstByMemberEntityIdOrderByEntranceTimeDesc(
        memberId);

    Map<String, Object> response = new HashMap<>();

    // 아예 이용 기록이 없는 경우
    if (latestHistory == null) {
      response.put("message", "해당 사용자의 주차 기록이 없습니다.");
      return response;
    }

    // exit time이 있는 경우 : 현재 입차된 상태 X
    if (latestHistory.getExitTime() != null) {
      response.put("isParked", false);
      response.put("parkingZone", null);
      response.put("entranceTime", null);
      response.put("cost", 0);
      response.put("message", "현재 주차 중인 상태가 아닙니다.");
      return response;
    }

    // exitTime이 없는 경우 : 현재 주차 중
    ParkingZoneEntity parkingZone = latestHistory.getParkingZoneEntity();

    /**
     * 1. parkingHistory(latestHistory)에서 parkingZoneId를 찾는다.
     * 1-2. 해당 parkingZoneId를 이용하여 parkingFeeRule에 들어가 해당되는 값을 찾는다.
     * 1-3. 여러 요금 규칙 튜플을 가져올 수 있음.
     */
    Long parkingZoneId = parkingZone.getId(); // 정상적으로 가져오고 있음
    List<ParkingFeeRuleEntity> feeRules = parkingFeeRuleRepository.findByParkingZoneEntityId(
        parkingZoneId); // 정상적으로 가져오고 있음

    System.out.println("parkingZoneId: " + parkingZoneId);
    System.out.println("feeRules: " + feeRules);
    System.out.println("Parking Fee Rules for Zone ID " + parkingZoneId + ":");
    feeRules.forEach(rule -> System.out.println(
        "Rule Name: " + rule.getRuleName() +
            ", CarType: " + rule.getCarType() +
            ", StartTime: " + rule.getStartTime() +
            ", EndTime: " + rule.getEndTime() +
            ", CostPerTimeSlot: " + rule.getCostPerTimeSlot() +
            ", CostTimeSlot: " + rule.getCostTimeSlot()
    ));

    /**
     * 2. parkingHistory(latestHistory)에서 carId를 찾아 해당하는 자동차의 carType을 가져온다.
     * 2-2. 가져온 요금 규칙 리스트에서 carType이 일치하지 않는 것은 제외한다.
     */
    CarEntity car = latestHistory.getCarEntity();
    CarType carType = car.getCarType();

    // applicableFeeRules : 해당 차종, 주차장에 해당하는 규칙들(튜플들)
    List<ParkingFeeRuleEntity> applicableFeeRules = feeRules.stream()
        .filter(rule -> rule.getCarType().equals(carType))
        .toList();

    // 3. 남은 튜플들을 출력하여 확인.
    System.out.println("==================================================================");
    System.out.println("Applicable Fee Rules for CarType: " + carType);
    System.out.println("해당되는 규칙들");
    for (ParkingFeeRuleEntity rule : applicableFeeRules) {
      System.out.println("Rule Name: " + rule.getRuleName() +
          ", CarType: " + rule.getCarType() +
          ", StartTime: " + rule.getStartTime() +
          ", EndTime: " + rule.getEndTime() +
          ", CostPerTimeSlot: " + rule.getCostPerTimeSlot() +
          ", CostTimeSlot: " + rule.getCostTimeSlot());
    }
    System.out.println("==================================================================");

    /**
     * 현재시간과 entranceTime을 비교하여 몇 분 차이인지 계산
     */
    LocalDateTime entranceTime = latestHistory.getEntranceTime(); // 입차 시간
    LocalDateTime now = LocalDateTime.now(); // 현재 시간
    Duration duration = Duration.between(entranceTime, now);
    long totalMinutesParked = duration.toMinutes(); // 주차한 총 시간(분)

    System.out.println("Entrance Time: " + entranceTime);
    System.out.println("Current Time: " + now);
    System.out.println("Total Minutes Parked: " + totalMinutesParked);

    /**
     * totalMinutesParked = 주차한 시간(분)
     * applicableFeeRules = 해당 차종, 주차장에 해당하는 규칙들(튜플들)
     * totalMinutesParked를 applicableFeeRules의 규칙들에 적용하여 요금을 계산하면 된다.
     * 일단 로직부터 구현해보자!!!
     */

    // totalMinutesParked= 30; // for test
    int totalCost = 0; // 총 요금
    long remainingMinutes = totalMinutesParked; // 남은 분량
    long temp_hourlyRate = 0;

    for (ParkingFeeRuleEntity rule : applicableFeeRules) {
      // 현재 규칙에서 사용 가능한 최대 분량
      long availableMinutes = rule.getEndTime() - rule.getStartTime() + 1;

      // 현재 남은 시간이 이 규칙에서 처리할 수 있는 범위를 초과하는 경우
      long usedMinutes = Math.min(remainingMinutes, availableMinutes);

      // 요금 추가 계산
      totalCost += (usedMinutes / rule.getCostTimeSlot()) * rule.getCostPerTimeSlot();
      temp_hourlyRate = rule.getCostPerTimeSlot();

      // 남은 시간 차감
      remainingMinutes -= usedMinutes;

      // 적용된 규칙 로그 출력
      System.out.println("Applied Rule: " + rule.getRuleName() +
          ", Used Minutes: " + usedMinutes +
          ", Added Cost: " + ((usedMinutes / rule.getCostTimeSlot()) * rule.getCostPerTimeSlot()) +
          ", Remaining Minutes: " + remainingMinutes);

      // 남은 시간이 0이면 종료
        if (remainingMinutes <= 0) {
            break;
        }
    }

    // 최종 요금 출력
    System.out.println("Total Parking Cost: " + totalCost);

    // JSON 응답 형식 맞추기 ============================== 위에서 다 처리하고 대입 =======================
    Map<String, Object> parkingZoneInfo = new HashMap<>();
    parkingZoneInfo.put("zoneName", parkingZone.getZoneName());
    parkingZoneInfo.put("hourlyRate", temp_hourlyRate); // 요금 정보는 현재 설정되지 않음
    parkingZoneInfo.put("entranceTime", latestHistory.getEntranceTime());
    parkingZoneInfo.put("cost", totalCost);

    response.put("parkingZone", parkingZoneInfo);
    return response;


  }
}

