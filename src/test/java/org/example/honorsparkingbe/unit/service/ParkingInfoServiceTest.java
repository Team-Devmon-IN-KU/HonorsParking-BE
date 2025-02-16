package org.example.honorsparkingbe.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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
import org.example.honorsparkingbe.util.converter.dto.ParkingFeeRuleDTOConverter;
import org.example.honorsparkingbe.util.converter.dto.ParkingZoneDTOConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("favoriteZone이 10개 미만이면서 1페이지 요청이 잘 수행 되었는지")
  void testGetParkingZonesWithFavoriteLessThan10() {
    // 1️⃣ Mock 데이터 설정 (favorite 주차장이 5개일 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(5);

    AtomicInteger counter = new AtomicInteger(1);

    // 1 3 5 7 9를 ID로 가지는 주차장 엔티티
    List<FavoriteParkingZoneEntity> updatedFavoriteZones = favoriteZones.stream()
        .peek(zone -> zone.getParkingZoneEntity().setId((long) counter.getAndAdd(2)))
        .collect(Collectors.toList());
    // PageImpl 생성: favoriteZones의 크기와 페이지 정보 전달
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(updatedFavoriteZones, pageable,
        favoriteZones.size());

    // Mock 레포지토리 세팅
    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(), eq(pageable)))
        .thenReturn(mockPage);

    when(parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        anyDouble(), anyDouble(), anyInt(), anyInt(), anyList()))
        .thenReturn(new ArrayList<>(Arrays.asList(2L, 4L, 6L, 8L, 10L)));

    when(parkingZoneRepository.count()).thenReturn(20L); // 전체 주차장 20개

    List<ParkingZoneEntity> totalReturnedParkingZones = createNonFavoriteZones(10);
    AtomicInteger counter2 = new AtomicInteger(1);
    // 1 ~ 10를 ID로 가지는 주차장 엔티티 반환
    List<ParkingZoneEntity> updatestotalReturnedParkingZones = totalReturnedParkingZones.stream()
        .peek(zone -> zone.setId((long) counter2.getAndAdd(1)))
        .collect(Collectors.toList());

    when(parkingZoneRepository.findAllByIdIn(anyList()))
        .thenReturn(updatestotalReturnedParkingZones);

    // DTO 변환 모킹
    mockDTOConversion();

    // 2️⃣ 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // 3️⃣ then
    // 즐겨찾기 주차장 조회 메서드 호출 검증
    verify(favoriteParkingZoneRepository).findAllByMemberEntity_IdOrderByIdAsc(eq(1L),
        eq(pageable));

// 가까운 주차장 ID 조회 메서드 호출 검증
    verify(parkingZoneRepository).findClosestParkingZonesIDWithExclusion(
        eq(request.getLatitude()), eq(request.getLongitude()), eq(5), eq(0),
        eq(Arrays.asList(1L, 3L, 5L, 7L, 9L)));

//// 전체 주차장 개수 조회 메서드 호출 검증
    verify(parkingZoneRepository).count();
//
//// ID 목록으로 주차장 조회 메서드 호출 검증
    verify(parkingZoneRepository).findAllByIdIn(
        eq(Arrays.asList(1L, 3L, 5L, 7L, 9L, 2L, 4L, 6L, 8L, 10L)));
    assertNotNull(response);
    assertEquals(0, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 5개 + 일반 5개 = 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
  }

//  @Test
//  @WithCustomMockUser
//  void testGetParkingZonesWithFavoriteNone() {
//    // 2️⃣ Mock 데이터 설정 (favorite 주차장이 없을 때)
//    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(0);
//    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(favoriteZones, pageable,
//        favoriteZones.size());
//
//    when(
//        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(), eq(pageable)))
//        .thenReturn(mockPage);
//
//    when(parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
//        anyDouble(), anyDouble(), eq(10), eq(0), anyList()))
//        .thenReturn();
//
//    when(parkingZoneRepository.count()).thenReturn(20L);
//
//    // DTO 변환 모킹
////    mockDTOConversion();
//
//    // 2️⃣ 서비스 메소드 호출
//    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
//        .builder()
//        .userId(1L)
//        .parkingZoneListRequest(request)
//        .build());
//
//    // 3️⃣ 결과 검증
//    assertNotNull(response);
//    assertEquals(0, response.getPagination().getCurrentPage());
//    assertEquals(20, response.getPagination().getTotalItems());
//    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 5개 + 일반 5개 = 10개
//    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
//  }

  @Test
  @WithCustomMockUser
  @DisplayName("DTO가 잘 반환되는지 확인")
  void notComplete() {

  }

  @Test
  @WithCustomMockUser
  @DisplayName("id 리스트로 불른 최종 리스트가 정렬이 잘 되는지 확인")
//  1) DTO가 잘 반환되는지 확인
//  2) id 리스트로 불른 최종 리스트가 정렬이 잘 되는지 확인
//  3) nonParkingzonesSlots잘 계산하는지 확인
//  4) 최종 ParkingZoneList가 목표한대로 잘 정렬되는지
//  5) 최종 ParkingZoneList가 목표개수와 값들이 일치하는지
//  6) Page관련 데이터가 잘 반환이 되었는지 확인
  void testGetParkingZonesBasic() {

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
