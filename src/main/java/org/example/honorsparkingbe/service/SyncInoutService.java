package org.example.honorsparkingbe.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.PayEntity;
import org.example.honorsparkingbe.domain.enums.PaymentType;
import org.example.honorsparkingbe.dto.request.SyncInoutRequest;
import org.example.honorsparkingbe.dto.response.SyncInoutResponse;
import org.example.honorsparkingbe.dto.response.SyncInoutResponse.ParkingEntry;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.PayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SyncInoutService {

  private final ParkingHistoryRepository parkingHistoryRepository;

  private final CarRepository carRepository;
  private final ParkingZoneRepository parkingZoneRepository;
  private final MemberRepository memberRepository;
  private final PayRepository payRepository;

  @Transactional
  public SyncInoutResponse syncParkingHistory(SyncInoutRequest request) {

    // 요청받은 차량 번호 리스트 추출
    List<String> vehicleNumbers = request.getInoutList().stream()
        .map(SyncInoutRequest.Inout::getVehicleNumber)
        .toList();

    // 1. 한 번의 쿼리로 모든 차량 정보 조회
    List<CarEntity> registeredCars = carRepository.findAllByCarNumberIn(vehicleNumbers);

    // 회원 차량의 입출차 기록이 없다면 빈배열 반환
    if (registeredCars.isEmpty()) {
      return SyncInoutResponse.builder()
          .ValidNonExitEntries(Collections.emptyList()) // 빈 리스트 반환
          .build();
    }

    // 차량을 Map<String, CarEntity> 형태로 변환 (빠른 검색을 위해)
    Map<String, CarEntity> carNumberToCarMap = registeredCars.stream()
        .collect(Collectors.toMap(CarEntity::getCarNumber, car -> car));

    // 회원 등록된 차량만 Response DTO내의 배열로 처리
    List<SyncInoutRequest.Inout> filteredNewMemberInoutList = request.getInoutList().stream()
        .filter(inout -> carNumberToCarMap.containsKey(inout.getVehicleNumber())) // 존재하는 차량만 필터링
        .toList();

    // 2. DB에서 차량 번호 리스트에 해당하는 회원 엔티티 조회
    List<MemberEntity> memberEntities = memberRepository.findAllByCarEntity_CarNumberIn(
        vehicleNumbers);

    // 3. 차량 번호(CarNumber)를 Key로, MemberEntity를 Value로 하는 Map 생성 (검색을 위한)
    Map<String, MemberEntity> carNumberToMemberMap = memberEntities.stream()
        .collect(Collectors.toMap(
            member -> member.getCarEntity().getCarNumber(), // Key: 차량 번호
            member -> member, // Value: MemberEntity
            (existing, replacement) -> existing // 중복 발생 시 기존 값 유지
        ));

    // 4. InOutList에서 parkinglotId 리스트 추출
    List<Long> parkinglotIds = filteredNewMemberInoutList.stream()
        .map(SyncInoutRequest.Inout::getParkinglotId)
        .distinct() // 중복 제거
        .toList();

    // 5. 한 번의 쿼리로 모든 주차장 정보 조회
    List<ParkingZoneEntity> parkingZones = parkingZoneRepository.findAllByIdIn(parkinglotIds);

    // 주차장을 Map<Long, ParkingZoneEntity> 형태로 변환
    Map<Long, ParkingZoneEntity> parkingZoneMap = parkingZones.stream()
        .collect(Collectors.toMap(ParkingZoneEntity::getId, zone -> zone));

    // PayEntity생성 후 DB에 저장
    List<PayEntity> payEntities = createPayEntities(filteredNewMemberInoutList,
        carNumberToMemberMap);
    List<PayEntity> savedPayEntities = payRepository.saveAll(payEntities);
    // 검색 속도를 위한 pay Map생성
    Map<Long, PayEntity> entryIdToPayEntityMap = savedPayEntities.stream()
        .collect(Collectors.toMap(
            payEntity -> payEntity.getId(),  // PayEntity의 id를 entryId로 사용
            payEntity -> payEntity           // PayEntity를 값으로 사용
        ));

    // 6. 필터링된 Inout Data를 ParkingHistory Entity 배열로 만들기
    List<ParkingHistoryEntity> parkingHistoryEntities = createParkingHistoryEntities(
        filteredNewMemberInoutList,
        carNumberToCarMap,
        carNumberToMemberMap,
        parkingZoneMap,
        entryIdToPayEntityMap
    );

    // 7. ParkingHistory를 저장 시도 하고 실패시에 에러를 던짐
    try {
      parkingHistoryRepository.saveAll(parkingHistoryEntities);
    } catch (Exception e) {
      throw new RuntimeException("주차 기록 저장 중 오류 발생", e);
    }

    return SyncInoutResponse.builder()
        .ValidNonExitEntries(
            parkingHistoryEntities.stream()
                .map(parkingHistoryEntity ->
                    ParkingEntry.builder().id(parkingHistoryEntity.getId()).build())
                .collect(Collectors.toList())  // List<Long>으로 수집
        )
        .build();
  }

  /**
   * @param filteredNewMemberInoutList
   * @param carNumberToCarMap
   * @param carNumberToMemberMap
   * @param parkingZoneMap
   * @param entryIdToPayEntityMap
   * @return 파라매터 정보를 통해서 ParkingHistoryEntities 배열을 반환
   */
  private List<ParkingHistoryEntity> createParkingHistoryEntities(
      List<SyncInoutRequest.Inout> filteredNewMemberInoutList,
      Map<String, CarEntity> carNumberToCarMap,
      Map<String, MemberEntity> carNumberToMemberMap,
      Map<Long, ParkingZoneEntity> parkingZoneMap,
      Map<Long, PayEntity> entryIdToPayEntityMap
  ) {

    return filteredNewMemberInoutList.stream()
        .map(inout -> {
          // 차량 번호를 이용해 MemberEntity를 carNumberToMemberMap에서 찾음
          MemberEntity member = carNumberToMemberMap.get(inout.getVehicleNumber());
          if (member == null) {
            throw new IllegalArgumentException(
                "Member not found for vehicle number: " + inout.getVehicleNumber());
          }

          CarEntity car = carNumberToCarMap.get(inout.getVehicleNumber());
          if (car == null) {
            throw new IllegalArgumentException(
                "Car not found for vehicle number: " + inout.getVehicleNumber());
          }
          // ParkingHistoryEntity 생성
          return ParkingHistoryEntity.builder()
              .id(inout.getEntryId()) // ID 설정
              .carEntity(car) // 차량 설정
              .memberEntity(member) // 멤버 설정
              .parkingZoneEntity(
                  parkingZoneMap.get(inout.getParkinglotId())) // ParkingZoneEntity 설정
              .entranceTime(inout.getEntryTime()) // 입차 시간 설정
              .exitTime(inout.getExitTime() != null //출차 시간 설정
                  ? inout.getExitTime()
                  : null)
              .paymentType(inout.getFee() != null
                  ? PaymentType.OTHER // 결제 유형 설정
                  : PaymentType.NONE)
              .payEntity(inout.getFee() != null
                  ? entryIdToPayEntityMap.get(inout.getEntryId())
                  : null)
              .build();
        })
        .collect(Collectors.toList()); // ParkingHistoryEntity 리스트로 반환


  }

  /**
   * @param filteredNewMemberInoutList 회원들의 입출차 기록 배열
   * @param carNumberToMemberMap       차량번호 -> 멤버 엔티티 HashMap
   * @return 입출차 기록중 결제 기록이 있는 것들은 PayEntity배열로 반환
   */
  private List<PayEntity> createPayEntities(List<SyncInoutRequest.Inout> filteredNewMemberInoutList,
      Map<String, MemberEntity> carNumberToMemberMap) {
    return filteredNewMemberInoutList.stream()
        .filter(inout -> inout.getFee() != null)
        .map(inout -> {
          MemberEntity member = carNumberToMemberMap.get(inout.getVehicleNumber());
          return PayEntity.builder()
              .amount(inout.getFee())
              .paidAt(inout.getPaidAt())
              .memberEntity(member)
              .build();
        })
        .collect(Collectors.toList());
  }
}
