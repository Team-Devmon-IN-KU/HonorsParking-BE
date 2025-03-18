package org.example.honorsparkingbe.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.dto.ParkingZoneListDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.repository.internal.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
import org.example.honorsparkingbe.util.converter.dto.ParkingFeeRuleDTOConverter;
import org.example.honorsparkingbe.util.converter.dto.ParkingZoneDTOConverter;
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
    // 1. DTO에서 요구 값 추출
    ParkingZoneListRequest query = extractQuery(parkingZoneListDTO);
    Long userId = extractUserId(parkingZoneListDTO);
    Pageable pageable = createPageable(parkingZoneListDTO);

    // 2. 즐겨찾기된 주차장 목록 불러오기
    List<ParkingZoneEntity> favoriteParkingZones = getFavoriteParkingZones(userId, pageable);
    // 즐겨찾기 주차장에서 ID들 추출
    List<Long> favoriteZonesIds = favoriteParkingZones.stream().map(ParkingZoneEntity::getId)
        .collect(Collectors.toList());
    // 3. nonParkingzonesSlots -> 일반 주차장이 필요한 개수
    int nonParkingzonesSlots = Math.min(
        10, // 최대 10이하
        Math.max(0, // 최소 0 이상
            pageable.getPageSize() - favoriteZonesIds.size())
    );

    // 4. 일반 주차장 ID배열 추출
    List<Long> nonFavoriteZonesIds = getNonFavoriteParkingZones(
        nonParkingzonesSlots, userId, favoriteZonesIds, query,
        pageable
    );
    // 5. ID 배열 합쳐 현재 페이지 필요한 ID 배열 추출
    List<Long> totalParkingZoneIds = Stream.concat(favoriteZonesIds.stream(),
            nonFavoriteZonesIds.stream())
        .collect(Collectors.toList());
    // 6. 필요 주차장 엔티티 불러오기
    List<ParkingZoneEntity> totalParkingZoneWithJoinTable = parkingZoneRepository.findAllByIdIn(
        totalParkingZoneIds);

    Map<Long, ParkingZoneEntity> entityMap = totalParkingZoneWithJoinTable.stream()
        .collect(Collectors.toMap(ParkingZoneEntity::getId, Function.identity(),
            (existing, replacement) -> existing));

    List<ParkingZoneEntity> sortedResults = totalParkingZoneIds.stream()
        .map(entityMap::get)
        .filter(Objects::nonNull)  // 혹시 존재하지 않는 ID가 있으면 제외
        .collect(Collectors.toList());

    // 7. 최종 주차장 DTO 만들기
    List<ParkingZoneDTO> parkingZones = new ArrayList<>();

    for (ParkingZoneEntity parkingZone : sortedResults) {

      ParkingZoneDTO parkingZoneZoneDTOArray = parkingZoneDTOConverter.toDTO(
          parkingZone,
          favoriteZonesIds.contains(parkingZone.getId()), // 즐겨찾기 여부
          parkingFeeRuleDTOConverter.toDtoList(parkingZone.getParkingFeeRuleEntities()));

      parkingZones.add(parkingZoneZoneDTOArray);
    }

    // 8. page관련 데이터 추출
    Long totalItems = parkingZoneRepository.count(); // 전체 주차장 수
    int totalPages =
        (totalItems.intValue() + pageable.getPageSize() - 1) / pageable.getPageSize(); // 총 페이지 수

    ParkingZoneListResponse.PaginationInfo paginationInfo = ParkingZoneListResponse.PaginationInfo.builder()
        .currentPage(pageable.getPageNumber())
        .totalPages(totalPages)
        .pagePerItem(pageable.getPageSize())
        .totalItems(totalItems.intValue())
        .build();

    // 9. 응답 객체 생성
    return ParkingZoneListResponse.builder()
        .parkingZones(parkingZones)
        .pagination(paginationInfo)
        .build();
  }

  private Pageable createPageable(ParkingZoneListDTO parkingZoneListDTO) {
    Long page = Optional.ofNullable(parkingZoneListDTO.getParkingZoneListRequest())
        .map(ParkingZoneListRequest::getPage)
        .orElse(0L); // 기본값 0
    int pageSize = 10; // 한 페이지에 보여줄 개수
    return PageRequest.of(page.intValue(), pageSize);
  }

  private Long extractUserId(ParkingZoneListDTO parkingZoneListDTO) {
    return parkingZoneListDTO.getUserId();
  }

  private ParkingZoneListRequest extractQuery(ParkingZoneListDTO parkingZoneListDTO) {
    return parkingZoneListDTO.getParkingZoneListRequest();
  }

  private List<ParkingZoneEntity> getFavoriteParkingZones(Long userId, Pageable pageable) {
    return favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(userId, pageable)
        .stream()
        .map(FavoriteParkingZoneEntity::getParkingZoneEntity)
        .collect(Collectors.toList());
  }

  private List<Long> getNonFavoriteParkingZones(
      int remainingSlots, // 주차장의 갯수 제약
      Long userId, //총 favoritezone 개수
      List<Long> favoriteZonesIds, // 제외할 즐겨찾기 주차장 ID들
      ParkingZoneListRequest query, // 필터링에 필요한 파라미터
      Pageable pageable // 페이징 처리
  ) {
    if (remainingSlots <= 0) {
      return Collections.emptyList();
    }
    List<Long> exclusionIds = favoriteZonesIds.isEmpty()
        ? Collections.singletonList(0L)
        : favoriteZonesIds;

    int totalFavoriteCount = favoriteParkingZoneRepository.countByMemberEntity_Id(userId);
    int effectiveOffset = Math.max(0, (pageable.getPageNumber() * 10) - totalFavoriteCount);
    return parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        query.getLatitude(),
        query.getLongitude(),
        remainingSlots,  // ⭐ 제한 개수 설정
        effectiveOffset,
        exclusionIds
    );
  }
}

