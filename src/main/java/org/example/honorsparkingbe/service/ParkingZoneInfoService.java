package org.example.honorsparkingbe.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.dto.ParkingZoneListDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.repository.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.example.honorsparkingbe.util.converter.dto.ParkingFeeRuleDTOConverter;
import org.example.honorsparkingbe.util.converter.dto.ParkingZoneDTOConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingZoneInfoService {

  //Repository
  private final FavoriteParkingZoneRepository favoriteParkingZoneRepository;
  private final ParkingZoneRepository parkingZoneRepository;
  private final ParkingFeeRuleRepository parkingFeeRuleRepository;
  //Util
  private final ParkingZoneDTOConverter parkingZoneDTOConverter;
  private final ParkingFeeRuleDTOConverter parkingFeeRuleDTOConverter;

  @Transactional(readOnly = true)
  public ParkingZoneListResponse getParkingZones(ParkingZoneListDTO parkingZoneListDTO) {
    // 1. DTO 값 추출
    ParkingZoneListRequest query = parkingZoneListDTO.getParkingZoneListRequest();
    Long userId = parkingZoneListDTO.getUserId();

    Long page = query.getPage() != null ? query.getPage() : 0; // 기본값 0
    int pageSize = 10; // 한 페이지에 보여줄 개수

    Pageable pageable = PageRequest.of(page.intValue(), pageSize);
    // 1️⃣ 즐겨찾기 주차장 엔티티 가져오기
    Page<FavoriteParkingZoneEntity> favoriteParkingZones = favoriteParkingZoneRepository
        .findAllByMemberEntity_IdOrderByIdAsc(userId, pageable);

    // SQl 몇개 불리나 start지점 확인
    // 즐겨찾기 주차장을 ParkingZoneEntity로 변환
    List<ParkingZoneEntity> favoriteZones = favoriteParkingZones.stream()
        .map(FavoriteParkingZoneEntity::getParkingZoneEntity)
        .collect(Collectors.toList());

    // 즐겨찾기 주차장 ID들 추출
    List<Long> favoriteZonesIds = favoriteZones.stream().map(ParkingZoneEntity::getId)
        .collect(Collectors.toList());

    long favoriteCount = favoriteZones.size();
    // 일반 주차장이 필요한 개수
    long remainingSlots = Math.max(0, pageSize - favoriteCount); // 최소값 0으로 설정

    // 최대 호출 개수가 10보다 커지지 않도록 설정
    remainingSlots = Math.min(remainingSlots, 10); // 최대값 10으로 제한

    // 2️⃣ 일반 주차장 가져오기 (남은 슬롯만큼)
    List<ParkingZoneEntity> nonFavoriteParkingZones;

    if (remainingSlots > 0) {
      nonFavoriteParkingZones = parkingZoneRepository.findClosestParkingZonesWithExclusion(
          query.getLatitude(),
          query.getLongitude(),
          remainingSlots,  // ⭐ 제한 개수 설정
          page * 10,
          favoriteZonesIds.size() == 0 ? new ArrayList<>(Collections.singletonList(0L))
              : favoriteZonesIds
      );
    } else {
      nonFavoriteParkingZones = Collections.emptyList();
    }
    // 일반 주차장 ID배열 추출
    List<Long> nonFavoriteZonesIds = nonFavoriteParkingZones.stream()
        .map(ParkingZoneEntity::getId)
        .collect(Collectors.toList());

    // 전체 타겟 ID 배열 합치기
    List<Long> totalParkingZoneIds = Stream.concat(favoriteZonesIds.stream(),
            nonFavoriteZonesIds.stream())
        .collect(Collectors.toList());

    // 해당 ID배열에 맞는 주차장 전체 요금규칙 불러오기 -- 레거시(비용 규칙말고 관계 테이블을 추가로 더 불러와야함
    List<ParkingFeeRuleEntity> rules = parkingFeeRuleRepository.findAllByParkingZoneEntityIdIn(
        totalParkingZoneIds);

    // ID별로 결과를 그룹화
    Map<Long, List<ParkingFeeRuleEntity>> ruleSetGroupedById = rules.stream()
        .collect(Collectors.groupingBy(rule -> rule.getParkingZoneEntity().getId()));

    // ID별로 모든 주차장에 대해 빈 리스트를 기본값으로 추가
    for (Long id : totalParkingZoneIds) {
      ruleSetGroupedById.putIfAbsent(id, new ArrayList<>());
    }

    List<ParkingZoneEntity> targetParkingZoneList = new ArrayList<>();
    targetParkingZoneList.addAll(favoriteZones);
    targetParkingZoneList.addAll(nonFavoriteParkingZones);

    // 4️⃣ 최종 주차장 리스트 만들기
    List<ParkingZoneDTO> parkingZones = new ArrayList<>();
    // 1. 즐겨찾기 주차장 변환 후 추가
//    totalParkingZoneIds의 ID들로 순회해야해
    for (ParkingZoneEntity parkingZone : targetParkingZoneList) {
      // 2차원 배열에서 인덱스에 맞는 값을 가져와서 사용

      // DTO 생성 (인덱스에 맞는 fee rules을 넣어준다)
      ParkingZoneDTO favoriteZoneDTO = parkingZoneDTOConverter.toDTO(
          parkingZone,
          favoriteZonesIds.contains(parkingZone.getId()),
          parkingFeeRuleDTOConverter.toDtoList(ruleSetGroupedById.get(parkingZone.getId())));

      // 결과 리스트에 추가
      parkingZones.add(favoriteZoneDTO);
    }

    // 3️⃣ page관련 데이터 추출
    long totalItems = parkingZoneRepository.count(); // 전체 아이템 수
    long totalPages = (totalItems + pageSize - 1) / pageSize; // 총 페이지 수
    // 5️⃣ 페이지네이션 정보 설정
    ParkingZoneListResponse.PaginationInfo paginationInfo = ParkingZoneListResponse.PaginationInfo.builder()
        .currentPage(page)
        .totalPages(totalPages)
        .pagePerItem((long) pageSize)
        .totalItems(totalItems)
        .build();

    // 6️⃣ 응답 객체 생성
    return ParkingZoneListResponse.builder()
        .parkingZones(parkingZones)
        .pagination(paginationInfo)
        .build();
  }
}

