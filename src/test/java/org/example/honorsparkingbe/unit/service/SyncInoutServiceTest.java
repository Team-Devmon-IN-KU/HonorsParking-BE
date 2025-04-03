package org.example.honorsparkingbe.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.PayEntity;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.domain.enums.PaymentType;
import org.example.honorsparkingbe.dto.request.SyncInoutRequest;
import org.example.honorsparkingbe.dto.request.SyncInoutRequest.Inout;
import org.example.honorsparkingbe.dto.response.SyncInoutResponse;
import org.example.honorsparkingbe.dto.response.SyncInoutResponse.ParkingEntry;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.PayRepository;
import org.example.honorsparkingbe.service.SyncInoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class SyncInoutServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(SyncInoutServiceTest.class);


  @InjectMocks
  private SyncInoutService syncInoutService;

  @Mock
  private CarRepository carRepository;
  @Mock
  private ParkingHistoryRepository parkingHistoryRepository;
  @Mock
  private MemberRepository memberRepository;
  @Mock
  private ParkingZoneRepository parkingZoneRepository;
  @Mock
  private PayRepository payRepository;

  CarEntity entryOnlyCar;
  CarEntity exitedCar;
  MemberEntity entryOnlyMember;
  MemberEntity exitMember;
  PayEntity exitCarpay;

  Inout entryOnlyInout;
  Inout exitedInout;
  Inout nonMemberIntout;

  ParkingZoneEntity sampleParkingZone;

  ParkingHistoryEntity entryOnlyParkingHistory;
  ParkingHistoryEntity exitedParkingHistory;

  @BeforeEach
  void setUp() {

    // 로그 출력 활성화

    // 4. 입차만 한 차량 엔티티
    entryOnlyCar = CarEntity.builder()
        .id(1L).carNumber("11가1111").carType(CarType.NORMAL).isElectric(false)
        .build();

    // 5. 출차까지 완료한 차량 엔티티
    exitedCar = CarEntity.builder()
        .id(2L).carNumber("22가2222").carType(CarType.LIGHT_CAR).isElectric(false)
        .build();

    entryOnlyMember = MemberEntity.builder()
        .id(1L).carEntity(entryOnlyCar).role(MemberRole.ROLE_USER).userName("entryOnlyMember")
        .authId("entryOnlyMember")
        .build();

    exitMember = MemberEntity.builder()
        .id(1L).carEntity(exitedCar).role(MemberRole.ROLE_USER).userName("exitMember")
        .authId("exitMember")
        .build();

    CityEntity seoul = CityEntity.builder().name("서울").build();
    DistrictEntity sampleDistrict = DistrictEntity.builder().name("중구").build();
    EupMyeonDongEntity sampleEupMyeonDong = EupMyeonDongEntity.builder().name("데브몬동").build();

    sampleParkingZone = ParkingZoneEntity.builder()
        .id(1L).latitude(37.5665).longitude(126.9780).zoneName("서울 주차장").size(100).maxCost(12000)
        .address("서울특별시").cityEntity(seoul).districtEntity(sampleDistrict)
        .eupMyeonDongEntity(sampleEupMyeonDong)
        .build();

    entryOnlyInout = Inout.builder()
        .vehicleNumber(entryOnlyCar.getCarNumber())
        .entryId(1L)
        .entryTime(LocalDateTime.of(2024, 4, 1, 10, 0)) // 예제 입차 시간
        .exitTime(null)
        .fee(null)
        .paidAt(null)
        .parkinglotId(1L)
        .build();

    exitedInout = Inout.builder()
        .vehicleNumber(exitedCar.getCarNumber())
        .entryId(2L)
        .entryTime(LocalDateTime.of(2024, 4, 1, 9, 0))  // 예제 입차 시간
        .exitTime(LocalDateTime.of(2024, 4, 1, 11, 0))  // 출차 시간
        .fee(5000) // 요금 예제
        .paidAt(LocalDateTime.of(2024, 4, 1, 10, 59)) // 결제 시간
        .parkinglotId(1L)
        .build();

    nonMemberIntout = Inout.builder()
        .vehicleNumber("33가3333") // 비회원 차량
        .entryId(3L)
        .entryTime(LocalDateTime.of(2024, 4, 1, 12, 0)) // 예제 입차 시간
        .exitTime(null)
        .fee(null)
        .paidAt(null)
        .parkinglotId(2L) // 다른 주차장 예제
        .build();

    exitCarpay = PayEntity.builder()
        .id(exitedInout.getEntryId())
        .amount(exitedInout.getFee())
        .build();

    entryOnlyParkingHistory = ParkingHistoryEntity.builder()
        .id(entryOnlyInout.getEntryId()).carEntity(entryOnlyCar).memberEntity(entryOnlyMember)
        .parkingZoneEntity(sampleParkingZone).entranceTime(entryOnlyInout.getEntryTime())
        .paymentType(PaymentType.NONE)
        .build();

    exitedParkingHistory = ParkingHistoryEntity.builder()
        .id(exitedInout.getEntryId()).carEntity(exitedCar).memberEntity(exitMember)
        .parkingZoneEntity(sampleParkingZone).entranceTime(exitedInout.getEntryTime())
        .exitTime(exitedInout.getExitTime()).paymentType(PaymentType.OTHER).payEntity(exitCarpay)
        .build();
  }

  @DisplayName("입출차 동시에 들어왔을 때 결제정보와 입차처리를 잘 하는가")
  @Test
  public void testSyncInoutWithSimultaneousEntryAndPayment() {

    // Given
    SyncInoutRequest syncInoutRequest = SyncInoutRequest.builder()
        .inoutList(List.of(entryOnlyInout, exitedInout))
        .build();

    when(carRepository.findAllByCarNumberIn(List.of(
        entryOnlyInout.getVehicleNumber(),
        exitedInout.getVehicleNumber())))
        .thenReturn(List.of(entryOnlyCar, exitedCar)
        );

    when(memberRepository.findAllByCarEntity_CarNumberIn(List.of(
        entryOnlyCar.getCarNumber(), exitedCar.getCarNumber())))
        .thenReturn(List.of(entryOnlyMember, exitMember));
    when(parkingZoneRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleParkingZone));
    when(payRepository.bulkInsertAndUpdate(anyList())).thenReturn(List.of(PayEntity.builder()
        .id(1L).amount(exitCarpay.getAmount()).id(exitCarpay.getId())
        .memberEntity(exitMember)
        .paidAt(exitCarpay.getPaidAt())
        .build()));
    doNothing().when(parkingHistoryRepository).bulkInsertAndUpdate(anyList());
    // WHEN
    SyncInoutResponse response = syncInoutService.syncParkingHistory(syncInoutRequest);

    // THEN
    assertNotNull(response);
    assertNotNull(response.getValidNonExitEntries());
    assertEquals(
        List.of(entryOnlyInout.getEntryId(), exitedInout.getEntryId()),  // 기대 값
        response.getValidNonExitEntries().stream()  // 실제 값
            .map(ParkingEntry::getId)  // ParkingEntry 객체의 id만 추출
            .collect(Collectors.toList())  // id 값만 리스트로 변환
    );

    // Verifying the repository calls
    verify(carRepository, times(1))
        .findAllByCarNumberIn(eq(List.of(entryOnlyCar.getCarNumber(), exitedCar.getCarNumber())));
    verify(memberRepository, times(1))
        .findAllByCarEntity_CarNumberIn(
            eq(List.of(entryOnlyCar.getCarNumber(), exitedCar.getCarNumber())));
    verify(parkingZoneRepository, times(1))
        .findAllByIdIn(eq(List.of(1L)));
    verify(payRepository, times(1))
        .bulkInsertAndUpdate(anyList());
    verify(parkingHistoryRepository, times(1))
        .bulkInsertAndUpdate(anyList());

  }

  @DisplayName("회원과 비회원차량이 섞였을 때 잘 필터링 하는가")
  @Test
  public void testFilterMemberAndNonMemberVehicles() {
    // 회원 차량과 비회원 차량을 구분하여 필터링이 제대로 이루어지는지 검증
    // Given
    SyncInoutRequest syncInoutRequest = SyncInoutRequest.builder()
        .inoutList(List.of(entryOnlyInout, exitedInout, nonMemberIntout))
        .build();

    when(carRepository.findAllByCarNumberIn(List.of(
        entryOnlyInout.getVehicleNumber(), exitedInout.getVehicleNumber(),
        nonMemberIntout.getVehicleNumber())))
        .thenReturn(List.of(entryOnlyCar, exitedCar)
        );

    when(memberRepository.findAllByCarEntity_CarNumberIn(List.of(
        entryOnlyCar.getCarNumber(), exitedCar.getCarNumber())))
        .thenReturn(List.of(entryOnlyMember, exitMember));
    when(parkingZoneRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleParkingZone));
    when(payRepository.bulkInsertAndUpdate(anyList())).thenReturn(List.of(PayEntity.builder()
        .id(1L).amount(exitCarpay.getAmount()).id(exitCarpay.getId())
        .memberEntity(exitMember)
        .paidAt(exitCarpay.getPaidAt())
        .build()));
    doNothing().when(parkingHistoryRepository).bulkInsertAndUpdate(anyList());
    // WHEN
    SyncInoutResponse response = syncInoutService.syncParkingHistory(syncInoutRequest);

    // THEN
    assertNotNull(response);
    assertNotNull(response.getValidNonExitEntries());
    assertEquals(
        List.of(entryOnlyInout.getEntryId(), exitedInout.getEntryId()),  // 기대 값
        response.getValidNonExitEntries().stream()  // 실제 값
            .map(ParkingEntry::getId)  // ParkingEntry 객체의 id만 추출
            .collect(Collectors.toList())  // id 값만 리스트로 변환
    );

    // Verifying the repository calls
    verify(carRepository, times(1))
        .findAllByCarNumberIn(eq(List.of(entryOnlyCar.getCarNumber(), exitedCar.getCarNumber(),
            nonMemberIntout.getVehicleNumber())));
    verify(memberRepository, times(1)).findAllByCarEntity_CarNumberIn(
        eq(List.of(entryOnlyCar.getCarNumber(), exitedCar.getCarNumber())));
    verify(parkingZoneRepository, times(1)).findAllByIdIn(eq(List.of(1L)));
    verify(payRepository, times(1)).bulkInsertAndUpdate(anyList());
    verify(parkingHistoryRepository, times(1)).bulkInsertAndUpdate(anyList());
  }

  @DisplayName("비회원 차량만 있을 때 빈배열이 반환되는지 확인")
  @Test
  public void testSyncInoutResponseNonMemberHistory() {
    // Given
    SyncInoutRequest syncInoutRequest = SyncInoutRequest.builder()
        .inoutList(List.of(nonMemberIntout))
        .build();
    when(carRepository.findAllByCarNumberIn(List.of(nonMemberIntout.getVehicleNumber())))
        .thenReturn(List.of(entryOnlyCar, exitedCar));

    // WHEN
    SyncInoutResponse response = syncInoutService.syncParkingHistory(syncInoutRequest);

    // THEN
    assertNotNull(response);
    assertNotNull(response.getValidNonExitEntries());
    assertTrue(response.getValidNonExitEntries().isEmpty());

    // Verifying the repository calls
    verify(carRepository, times(1))
        .findAllByCarNumberIn(eq(List.of(nonMemberIntout.getVehicleNumber())));
  }

  @DisplayName("같은 회원 입출차 ID값이 들어왔을 때 1개만 처리 되는가")
  @Test
  public void testDuplicatetryIdHandling() {
    // 동일한 ID가 여러 번 들어왔을 때, 중복 처리 혹은 오류가 발생하지 않고 정상적으로 처리되는지 검증
    // Given
    SyncInoutRequest syncInoutRequest = SyncInoutRequest.builder()
        .inoutList(List.of(entryOnlyInout, entryOnlyInout))
        .build();

    when(carRepository.findAllByCarNumberIn(List.of(
        entryOnlyInout.getVehicleNumber(),
        entryOnlyInout.getVehicleNumber())))
        .thenReturn(List.of(entryOnlyCar));

    when(memberRepository.findAllByCarEntity_CarNumberIn(List.of(entryOnlyCar.getCarNumber())))
        .thenReturn(List.of(entryOnlyMember));
    when(parkingZoneRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(sampleParkingZone));
    doNothing().when(parkingHistoryRepository).bulkInsertAndUpdate(anyList());

    // WHEN
    SyncInoutResponse response = syncInoutService.syncParkingHistory(syncInoutRequest);

    // THEN
    assertNotNull(response);
    assertNotNull(response.getValidNonExitEntries());
    assertEquals(
        List.of(entryOnlyInout.getEntryId()),  // 기대 값
        response.getValidNonExitEntries().stream()  // 실제 값
            .map(ParkingEntry::getId)  // ParkingEntry 객체의 id만 추출
            .collect(Collectors.toList())  // id 값만 리스트로 변환
    );

    // Verifying the repository calls
    verify(carRepository, times(1))
        .findAllByCarNumberIn(
            eq(List.of(entryOnlyCar.getCarNumber(), entryOnlyCar.getCarNumber())));
    verify(memberRepository, times(1))
        .findAllByCarEntity_CarNumberIn(eq(List.of(entryOnlyCar.getCarNumber())));
    verify(parkingZoneRepository, times(1))
        .findAllByIdIn(eq(List.of(1L)));
    verify(parkingHistoryRepository, times(1))
        .bulkInsertAndUpdate(anyList());

  }
}
