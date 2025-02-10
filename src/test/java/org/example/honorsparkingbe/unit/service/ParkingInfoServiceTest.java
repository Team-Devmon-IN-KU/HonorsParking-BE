package org.example.honorsparkingbe.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.ParkingFeeRuleDTO;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;
import org.example.honorsparkingbe.dto.ParkingZoneListDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.mock.WithCustomMockUser;
import org.example.honorsparkingbe.repository.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.example.honorsparkingbe.service.ParkingZoneInfoService;
import org.example.honorsparkingbe.util.converter.ParkingFeeRuleDTOConverter;
import org.example.honorsparkingbe.util.converter.ParkingZoneDTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ParkingInfoServiceTest {

  @Mock
  private FavoriteParkingZoneRepository favoriteParkingZoneRepository;
  @Mock
  private ParkingZoneRepository parkingZoneRepository;
  @Mock
  private ParkingFeeRuleRepository parkingFeeRuleRepository;
  @Mock
  private ParkingZoneDTOConverter parkingZoneDTOConverter;
  @Mock
  private ParkingFeeRuleDTOConverter parkingFeeRuleDTOConverter;

  @InjectMocks
  private ParkingZoneInfoService parkingZoneInfoService;

  private ParkingZoneListRequest request;

  Pageable pageable = PageRequest.of(0, 10);

  @BeforeEach
  void setUp() {
    // 기본 request 객체 설정
    request = ParkingZoneListRequest.builder()
        .latitude(37.5665)
        .longitude(126.9780)
        .build();
  }

  @Test
  void testGetParkingZonesWithFavoriteLessThan10() {
    // 1️⃣ Mock 데이터 설정 (favorite 주차장이 5개일 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(5);
    // PageImpl 생성: favoriteZones의 크기와 페이지 정보 전달
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(favoriteZones, pageable,
        favoriteZones.size());

    // Mock 설정
    // 일반 주차장이 20개일때
    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(), eq(pageable)))
        .thenReturn(mockPage);

    when(parkingZoneRepository.findClosestParkingZonesWithExclusion(
        anyDouble(), anyDouble(), eq(5L), eq(0L), anyList()))
        .thenReturn(createNonFavoriteZones(5));

    when(parkingZoneRepository.count()).thenReturn(20L); // 전체 주차장 20개

    // DTO 변환 모킹
    mockDTOConversion();

    // 2️⃣ 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // 3️⃣ 결과 검증
    assertNotNull(response);
    assertEquals(0, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 5개 + 일반 5개 = 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
  }

  @Test
  @WithCustomMockUser
  void testGetParkingZonesWithFavoriteNone() {
    // 2️⃣ Mock 데이터 설정 (favorite 주차장이 없을 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(0);
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(favoriteZones, pageable,
        favoriteZones.size());

    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(), eq(pageable)))
        .thenReturn(mockPage);

    when(parkingZoneRepository.findClosestParkingZonesWithExclusion(
        anyDouble(), anyDouble(), eq(10L), eq(0L), anyList()))
        .thenReturn(createNonFavoriteZones(10));

    when(parkingZoneRepository.count()).thenReturn(20L);

    // DTO 변환 모킹
    mockDTOConversion();

    // 2️⃣ 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // 3️⃣ 결과 검증
    assertNotNull(response);
    assertEquals(0, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 5개 + 일반 5개 = 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
  }

  // Helper 메서드
  private List<FavoriteParkingZoneEntity> createFavoriteZones(int count) {
    List<FavoriteParkingZoneEntity> zones = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      FavoriteParkingZoneEntity entity = new FavoriteParkingZoneEntity();
      entity.setParkingZoneEntity(new ParkingZoneEntity()); // 주차장 엔티티 설정
      zones.add(entity);
    }
    return zones;
  }

  private List<ParkingZoneEntity> createNonFavoriteZones(int count) {
    List<ParkingZoneEntity> zones = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      zones.add(new ParkingZoneEntity()); // 일반 주차장 엔티티 설정
    }
    return zones;
  }

  private void mockDTOConversion() {
    when(parkingZoneDTOConverter.toDTO(any(), anyBoolean(), anyList()))
        .thenReturn(new ParkingZoneDTO());

    when(parkingFeeRuleDTOConverter.toDtoList(any()))
        .thenReturn(List.of(new ParkingFeeRuleDTO()));
  }
}
