package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.*;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.repository.internal.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrentInfoServiceTest {

  @Mock
  private ParkingHistoryRepository parkingHistoryRepository;

  @Mock
  private ParkingFeeRuleRepository parkingFeeRuleRepository;

  @InjectMocks
  private CurrentInfoService currentInfoService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetCurrentParkingInfo_NoHistory() {
    when(parkingHistoryRepository.findFirstByMemberEntityIdOrderByEntranceTimeDesc(1L)).thenReturn(
        null);

    Map<String, Object> result = currentInfoService.getCurrentParkingInfo(1L);

    assertEquals("해당 사용자의 주차 기록이 없습니다.", result.get("message"));
  }

  @Test
  void testGetCurrentParkingInfo_NotParked() {
    ParkingHistoryEntity history = mock(ParkingHistoryEntity.class);
    when(history.getExitTime()).thenReturn(LocalDateTime.now());

    when(parkingHistoryRepository.findFirstByMemberEntityIdOrderByEntranceTimeDesc(2L)).thenReturn(
        history);

    Map<String, Object> result = currentInfoService.getCurrentParkingInfo(2L);

    assertEquals(false, result.get("isParked"));
    assertEquals("현재 주차 중인 상태가 아닙니다.", result.get("message"));
  }

  @Test
  void testGetCurrentParkingInfo_CurrentlyParked() {
    // 1. 테스트 데이터 설정
    Long memberId = 2L; // member2가 현재 주차 중 (exitTime이 null)
    Long parkingZoneId = 2L; // ParkingZoneEntity ID
    LocalDateTime entranceTime = LocalDateTime.of(2025, 2, 10, 10, 0); // 입차 시간

    // LocalDateTime now = LocalDateTime.of(2025, 2, 23, 21, 53); // 실제 시간 제대로 가져오지 못할경우 현재 시간 직접 삽입
    LocalDateTime now = LocalDateTime.now();

    // ParkingZoneEntity (주차장)
    ParkingZoneEntity parkingZone = mock(ParkingZoneEntity.class);
    when(parkingZone.getId()).thenReturn(parkingZoneId);
    when(parkingZone.getZoneName()).thenReturn("Busan Haeundae-gu Centum City Parking Lot");

    // CarEntity (차량)
    CarEntity car = mock(CarEntity.class);
    when(car.getCarType()).thenReturn(CarType.MIDSIZE);

    // ParkingHistoryEntity (주차 이력)
    ParkingHistoryEntity history = mock(ParkingHistoryEntity.class);
    when(history.getParkingZoneEntity()).thenReturn(parkingZone);
    when(history.getCarEntity()).thenReturn(car);
    when(history.getEntranceTime()).thenReturn(entranceTime);
    when(history.getExitTime()).thenReturn(null); // 출차 기록 없음 (현재 주차 중)

    when(parkingHistoryRepository.findFirstByMemberEntityIdOrderByEntranceTimeDesc(memberId))
        .thenReturn(history);

    // 🚀 요금 규칙 설정 (더미 데이터 반영)

    // 1. 0~30분: 30분당 600원
    ParkingFeeRuleEntity feeRule1 = mock(ParkingFeeRuleEntity.class);
    when(feeRule1.getCarType()).thenReturn(CarType.MIDSIZE);
    when(feeRule1.getStartTime()).thenReturn(0);
    when(feeRule1.getEndTime()).thenReturn(30);
    when(feeRule1.getCostTimeSlot()).thenReturn(30); // 30분 단위
    when(feeRule1.getCostPerTimeSlot()).thenReturn(600);

    // 2. 31~120분: 10분당 250원
    ParkingFeeRuleEntity feeRule2 = mock(ParkingFeeRuleEntity.class);
    when(feeRule2.getCarType()).thenReturn(CarType.MIDSIZE);
    when(feeRule2.getStartTime()).thenReturn(31);
    when(feeRule2.getEndTime()).thenReturn(120);
    when(feeRule2.getCostTimeSlot()).thenReturn(10); // 10분 단위
    when(feeRule2.getCostPerTimeSlot()).thenReturn(250);

    // 3. 121분 이후: 10분당 350원
    ParkingFeeRuleEntity feeRule3 = mock(ParkingFeeRuleEntity.class);
    when(feeRule3.getCarType()).thenReturn(CarType.MIDSIZE);
    when(feeRule3.getStartTime()).thenReturn(121);
    when(feeRule3.getEndTime()).thenReturn(Integer.MAX_VALUE); // 무제한 시간
    when(feeRule3.getCostTimeSlot()).thenReturn(10); // 10분 단위
    when(feeRule3.getCostPerTimeSlot()).thenReturn(350);

    when(parkingFeeRuleRepository.findByParkingZoneEntityId(parkingZoneId))
        .thenReturn(List.of(feeRule1, feeRule2, feeRule3));

    // 2. 서비스 메서드 실행
    Map<String, Object> result = currentInfoService.getCurrentParkingInfo(memberId);

    // 3. 결과 검증
    assertNotNull(result);
    assertTrue(result.containsKey("parkingZone"));

    Map<String, Object> parkingZoneInfo = (Map<String, Object>) result.get("parkingZone");
    assertEquals("Busan Haeundae-gu Centum City Parking Lot", parkingZoneInfo.get("zoneName"));

    // 🚀 현재 시간과 입차 시간 기반으로 실제 주차 시간을 계산
    long totalMinutesParked = Duration.between(entranceTime, now).toMinutes();

    // ✅ 요금 계산 로직 (규칙 기반)
    int calculatedCost = 0;

    // 1. 0~30분: 30분당 600원
    if (totalMinutesParked > 0) {
      long minutesUsed = Math.min(totalMinutesParked, 30);
      calculatedCost += (minutesUsed / 30) * 600;
      totalMinutesParked -= minutesUsed;
    }
    System.out.println("current: " + totalMinutesParked);

    // 2. 31~120분: 10분당 250원
    if (totalMinutesParked > 0) {
      long minutesUsed = Math.min(totalMinutesParked, 90);
      calculatedCost += (minutesUsed / 10) * 250;
      totalMinutesParked -= minutesUsed;
    }
    System.out.println("current: " + totalMinutesParked);

    // 3. 121분 이후: 10분당 350원
    if (totalMinutesParked > 0) {
      calculatedCost += (totalMinutesParked / 10) * 350;
    }

    int actualCost = ((Number) parkingZoneInfo.get("cost")).intValue();

    // ✅ 예상 요금과 실제 계산된 요금을 비교
    assertEquals(calculatedCost, actualCost);

    // 디버깅용 출력
    System.out.println("Total Minutes Parked: " + Duration.between(entranceTime, now).toMinutes());
    System.out.println("Calculated Cost: " + calculatedCost);
    System.out.println("Actual Cost: " + actualCost);

    System.out.println("테스트 완료: 현재 주차 중인 경우 (요금: " + actualCost + "원)");
  }


}
