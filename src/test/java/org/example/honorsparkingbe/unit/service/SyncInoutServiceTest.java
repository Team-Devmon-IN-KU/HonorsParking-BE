package org.example.honorsparkingbe.unit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SyncInoutServiceTest {

  @DisplayName("입출차 동시에 들어왔을 때 결제정보와 입차처리를 잘 하는가")
  @Test
  public void testSyncInoutWithSimultaneousEntryAndPayment() {
    // 입차와 결제 정보가 동시에 처리되는 경우, 결제 정보와 입차 기록이 제대로 처리되는지 검증
  }

  @DisplayName("회원과 비회원차량이 섞였을 때 잘 필터링 하는가")
  @Test
  public void testFilterMemberAndNonMemberVehicles() {
    // 회원 차량과 비회원 차량을 구분하여 필터링이 제대로 이루어지는지 검증
    // 비회원 차량이 잘 필터링되고 회원 차량만 입출차 기록이 처리되는지 확인
  }

  @DisplayName("같은 ID값이 들어왔을 때 동일한 처리 로직이 실행되는가")
  @Test
  public void testDuplicateEntryIdHandling() {
    // 동일한 ID가 여러 번 들어왔을 때, 중복 처리 혹은 오류가 발생하지 않고 정상적으로 처리되는지 검증
    // 중복된 entryId에 대해서 어떻게 처리되는지 검토
  }

  @DisplayName("차량 번호가 존재하지 않는 경우 빈 리스트가 반환되는지 확인")
  @Test
  public void testNoVehicleDataFound() {
    // 등록되지 않은 차량 번호에 대한 처리가 제대로 이루어지고, 빈 리스트가 반환되는지 확인
    // registeredCars가 비어 있을 경우 빈 리스트가 반환되는지 검증
  }

  @DisplayName("회원이 등록된 차량 번호로만 처리되는지 확인")
  @Test
  public void testProcessOnlyRegisteredVehicles() {
    // 회원 차량 번호만 처리되는지, 비회원 차량 번호가 필터링되는지 확인
    // `filteredNewMemberInoutList`가 회원 차량에 대해서만 잘 필터링되는지 검증
  }

  @DisplayName("InOut 리스트에서 중복되는 주차장 ID를 처리하는지 확인")
  @Test
  public void testHandleDuplicateParkinglotIds() {
    // `filteredNewMemberInoutList`에서 중복된 주차장 ID를 처리하고, 실제로 DB에서 중복된 주차장 정보가 조회되는지 확인
    // `distinct()`가 잘 적용되어 중복된 주차장 ID가 처리되는지 확인
  }

  @DisplayName("PayEntity가 정상적으로 DB에 저장되는지 확인")
  @Test
  public void testPayEntitySaving() {
    // `payRepository.saveAll()`을 통해 `PayEntity`가 정상적으로 저장되는지 확인
    // `PayEntity` 저장 후 반환되는 값이 정확한지, 저장 후 조회하여 값을 비교
  }

  @DisplayName("ParkingHistoryEntity 배열이 제대로 생성되는지 확인")
  @Test
  public void testParkingHistoryEntityCreation() {
    // `createParkingHistoryEntities()`가 필터링된 입출차 데이터로 `ParkingHistoryEntity`를 잘 생성하는지 확인
    // 생성된 엔티티들이 예상대로 필드가 채워졌는지 검증
  }

  @DisplayName("ParkingHistory 저장 시 에러가 발생하는지 확인")
  @Test
  public void testParkingHistorySavingError() {
    // `parkingHistoryRepository.saveAll()`을 호출할 때 에러가 발생하는지 테스트
    // 저장 중 예외가 발생하면 `RuntimeException`이 던져지는지 확인
  }

  @DisplayName("SyncInoutResponse가 정상적으로 반환되는지 확인")
  @Test
  public void testSyncInoutResponse() {
    // `SyncInoutResponse`가 정상적으로 반환되고, `ValidNonExitEntries`에 올바른 값들이 포함되는지 확인
    // 반환된 Response의 내용이 기대한대로 구성되는지 검증
  }

}
