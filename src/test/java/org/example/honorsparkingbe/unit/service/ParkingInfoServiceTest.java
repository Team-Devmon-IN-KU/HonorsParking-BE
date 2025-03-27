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
import org.example.honorsparkingbe.repository.internal.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
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
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class ParkingInfoServiceTest {

  @Mock
  private FavoriteParkingZoneRepository favoriteParkingZoneRepository;
  @Mock
  private ParkingZoneRepository parkingZoneRepository;
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
    // given
    // 1. Mock favoriteZones 관련 데이터 설정 (favorite 주차장이 5개일 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(5);

    AtomicInteger counter = new AtomicInteger(1);

    // 1 3 5 7 9를 ID로 가지는 favoriteZones-parkingZone 엔티티
    List<FavoriteParkingZoneEntity> updatedFavoriteZones = favoriteZones.stream()
        .peek(zone -> zone.getParkingZoneEntity().setId((long) counter.getAndAdd(2)))
        .collect(Collectors.toList());
    // PageImpl로 완성
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(updatedFavoriteZones, pageable,
        favoriteZones.size());

    //2. Mock 레포지토리 세팅
    // Mock favoriteZones Return
    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(), eq(pageable)))
        .thenReturn(mockPage);
    // 2 4 6 8 10을 ID로 가지는 non-favoriteZones-parkingZone 엔티티
    when(parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        anyDouble(), anyDouble(), anyInt(), anyInt(), anyList()))
        .thenReturn(new ArrayList<>(Arrays.asList(2L, 4L, 6L, 8L, 10L)));
    // 총 주차장 개수 20개로 mocking
    when(parkingZoneRepository.count()).thenReturn(20L); // 전체 주차장 20개
    // 최종 반환 주차장 mocking
    List<ParkingZoneEntity> totalReturnedParkingZones = createNonFavoriteZones(10);
    AtomicInteger counter2 = new AtomicInteger(1);
    // 1 ~ 10를 ID로 가지는 주차장 엔티티 반환
    List<ParkingZoneEntity> updatestotalReturnedParkingZones = totalReturnedParkingZones.stream()
        .peek(zone -> zone.setId((long) counter2.getAndAdd(1)))
        .collect(Collectors.toList());

    when(parkingZoneRepository.findAllByIdIn(anyList()))
        .thenReturn(updatestotalReturnedParkingZones);

    when(favoriteParkingZoneRepository.countByMemberEntity_Id(1L)).thenReturn(5);
    // when
    // 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // then
    // 즐겨찾기 주차장 조회 메서드 호출 검증
    verify(favoriteParkingZoneRepository).findAllByMemberEntity_IdOrderByIdAsc(eq(1L),
        eq(pageable));

    // 가까운 주차장 ID 조회 메서드 호출 검증
    verify(parkingZoneRepository).findClosestParkingZonesIDWithExclusion(
        eq(request.getLatitude()), eq(request.getLongitude()), eq(5), eq(0),
        eq(Arrays.asList(1L, 3L, 5L, 7L, 9L)));

    // 전체 주차장 개수 조회 메서드 호출 검증
    verify(parkingZoneRepository).count();
    // ID 목록으로 주차장 조회 메서드 호출 검증
    verify(parkingZoneRepository).findAllByIdIn(
        eq(Arrays.asList(1L, 3L, 5L, 7L, 9L, 2L, 4L, 6L, 8L, 10L)));
    assertNotNull(response);
    assertEquals(0, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 5개 + 일반 5개 = 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
  }

  @Test
  @DisplayName("favoriteZone이 10개 미만이면서 2페이지 요청이 잘 수행 되었는지")
  void testGetParkingZonesWithFavoriteLessThan10AndRequestPage2() {

    // given
    // given-1. 2번째 페이지 요청이 가도록 관련 page request값 수정
    request.setPage(1L);
    pageable = PageRequest.of(1, 10);
    // given-2. Mock favoriteZones 관련 데이터 설정 (favorite 주차장이 5개일 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(0);
    // PageImpl로 Mock page완성
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(favoriteZones, pageable,
        favoriteZones.size());
    // Mock favoriteZones page Return
    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(),
            any(PageRequest.class)))
        .thenReturn(mockPage);

    //given-2. 1L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L을 ID로 가지는
    // non-favoriteZones-parkingZone 엔티티 반환 when절 세팅
    when(parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        anyDouble(), anyDouble(), anyInt(), anyInt(), anyList()))
        .thenReturn(
            new ArrayList<>(Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L)));

    // given-3. 총 주차장 개수 20개로 mocking
    when(parkingZoneRepository.count()).thenReturn(20L); // 전체 주차장 20개

    //  given-4. 최종 반환 주차장 mocking
    //ID 배열 (2, 4, 6, ..., 15)로 ID 값을 설정
    List<Long> ids = Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L);

    // given-5. 최종 ParkingZone 반환 mocking
    List<ParkingZoneEntity> totalReturnedParkingZones = createNonFavoriteZones(10);
    AtomicInteger counter = new AtomicInteger(0);  // 시작값을 0으로 설정
    // ID 값을 순차적으로 설정하여 ParkingZoneEntity 객체에 적용
    List<ParkingZoneEntity> updatedParkingZones = totalReturnedParkingZones.stream()
        .peek(zone -> zone.setId(ids.get(counter.getAndAdd(1))))  // 순차적으로 ID 할당
        .collect(Collectors.toList());
    when(parkingZoneRepository.findAllByIdIn(anyList()))
        .thenReturn(updatedParkingZones);

    // given-6. Favorite-ParkingZone 5개 반환 mocking
    when(favoriteParkingZoneRepository.countByMemberEntity_Id(anyLong())).thenReturn(5);

    // when
    // 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // then
    // 즐겨찾기 주차장 조회 메서드 호출 검증
    verify(favoriteParkingZoneRepository).findAllByMemberEntity_IdOrderByIdAsc(eq(1L),
        eq(pageable));

    // 가까운 주차장 ID 조회 메서드 호출 검증
    verify(parkingZoneRepository).findClosestParkingZonesIDWithExclusion(
        eq(request.getLatitude()), eq(request.getLongitude()), eq(10), eq(5),
        eq(Arrays.asList(0L)));

    // 전체 주차장 개수 조회 메서드 호출 검증
    verify(parkingZoneRepository).count();
    // ID 목록으로 주차장 조회 메서드 호출 검증
    verify(parkingZoneRepository).findAllByIdIn(
        eq(Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L)));

    assertNotNull(response);
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 일반 10개 = 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인

  }

  @Test
  @DisplayName("favoriteZone이 10개 초과이면서 1페이지 요청이 잘 수행 되었는지")
  void testGetParkingZonesWithFavoriteGreaterThan10AndRequestPage1() {
    // given
    // 1. Mock favoriteZones 관련 데이터 설정 (favorite 주차장이 15개일 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(10);

    AtomicInteger counter = new AtomicInteger(1);

    // 1 - 10를 ID로 가지는 favoriteZones-parkingZone 엔티티
    List<FavoriteParkingZoneEntity> updatedFavoriteZones = favoriteZones.stream()
        .peek(zone -> zone.getParkingZoneEntity().setId((long) counter.getAndAdd(1)))
        .collect(Collectors.toList());
    // PageImpl로 완성
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(updatedFavoriteZones, pageable,
        favoriteZones.size());

    //2. Mock 레포지토리 세팅
    // Mock favoriteZones Return
    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(),
            any(PageRequest.class)))
        .thenReturn(mockPage);

    // 총 주차장 개수 20개로 mocking
    when(parkingZoneRepository.count()).thenReturn(20L); // 전체 주차장 20개
    // 최종 반환 주차장 mocking
    //ID 배열 (1 - 10)로 ID 값을 설정
    List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

    // 1 ~ 10을 순차적으로 ID로 가지는 ParkingZoneEntity 반환
    List<ParkingZoneEntity> totalReturnedParkingZones = createNonFavoriteZones(10);
    AtomicInteger counter2 = new AtomicInteger(0);  // 시작값을 0으로 설정
    // ID 값을 순차적으로 설정하여 ParkingZoneEntity 객체에 적용
    List<ParkingZoneEntity> updatedParkingZones = totalReturnedParkingZones.stream()
        .peek(zone -> zone.setId(ids.get(counter2.getAndAdd(1))))  // 순차적으로 ID 할당
        .collect(Collectors.toList());

    when(parkingZoneRepository.findAllByIdIn(anyList()))
        .thenReturn(updatedParkingZones);

    // when
    // 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // then
    // 즐겨찾기 주차장 조회 메서드 호출 검증
    verify(favoriteParkingZoneRepository).findAllByMemberEntity_IdOrderByIdAsc(eq(1L),
        eq(pageable));

    // 전체 주차장 개수 조회 메서드 호출 검증
    verify(parkingZoneRepository).count();
    // ID 목록으로 주차장 조회 메서드 호출 검증
    verify(parkingZoneRepository).findAllByIdIn(
        eq(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)));
    assertNotNull(response);
    assertEquals(0, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
  }

  @Test
  @DisplayName("favoriteZone이 10개 초과이면서 2페이지 요청이 잘 수행 되었는지")
  void testGetParkingZonesWithFavoriteGreaterThan10AndRequestPage2() {

    // given
    // 2번째 페이지 요청이 가도록 관련 pager request값 수정
    request.setPage(1L);
    pageable = PageRequest.of(1, 10);

    // 1. Mock favoriteZones 관련 데이터 설정 (favorite 주차장이 15개일 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(5);
    AtomicInteger counter = new AtomicInteger(16);
    // 15 - 20를 ID로 가지는 favoriteZones-parkingZone 엔티티
    List<FavoriteParkingZoneEntity> updatedFavoriteZones = favoriteZones.stream()
        .peek(zone -> zone.getParkingZoneEntity().setId((long) counter.getAndAdd(1)))
        .collect(Collectors.toList());
    // PageImpl로 완성
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(updatedFavoriteZones, pageable,
        favoriteZones.size());

    //2. Mock 레포지토리 세팅
    // Mock favoriteZones Return
    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(),
            any(PageRequest.class)))
        .thenReturn(mockPage);
    // 2 4 6 8 10을 ID로 가지는 non-favoriteZones-parkingZone 엔티티
    when(parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        anyDouble(), anyDouble(), anyInt(), anyInt(), anyList()))
        .thenReturn(new ArrayList<>(Arrays.asList(2L, 4L, 6L, 8L, 10L)));
    // 총 주차장 개수 20개로 mocking
    when(parkingZoneRepository.count()).thenReturn(20L); // 전체 주차장 20개
    // 즐겨찾기 주차장 15개
    when(favoriteParkingZoneRepository.countByMemberEntity_Id(anyLong())).thenReturn(
        15);

    // 최종 반환 주차장 mocking
    //ID 배열로 ID 값을 설정
    List<Long> ids = Arrays.asList(16L, 17L, 18L, 19L, 20L, 2L, 4L, 6L, 8L, 10L);

    // 1 ~ 10을 순차적으로 ID로 가지는 ParkingZoneEntity 반환
    List<ParkingZoneEntity> totalReturnedParkingZones = createNonFavoriteZones(10);
    AtomicInteger counter2 = new AtomicInteger(0);  // 시작값을 0으로 설정
    // ID 값을 순차적으로 설정하여 ParkingZoneEntity 객체에 적용
    List<ParkingZoneEntity> updatedParkingZones = totalReturnedParkingZones.stream()
        .peek(zone -> zone.setId(ids.get(counter2.getAndAdd(1))))  // 순차적으로 ID 할당
        .collect(Collectors.toList());

    when(parkingZoneRepository.findAllByIdIn(anyList()))
        .thenReturn(updatedParkingZones);
    when(favoriteParkingZoneRepository.countByMemberEntity_Id(1L)).thenReturn(15);
    // when
    // 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // then
    // 즐겨찾기 주차장 조회 메서드 호출 검증
    verify(favoriteParkingZoneRepository).findAllByMemberEntity_IdOrderByIdAsc(eq(1L),
        eq(pageable));
    // 가까운 주차장 ID 조회 메서드 호출 검증
    verify(parkingZoneRepository).findClosestParkingZonesIDWithExclusion(
        eq(request.getLatitude()), eq(request.getLongitude()), eq(5), eq(0),
        eq(Arrays.asList(16L, 17L, 18L, 19L, 20L)));
    // 전체 주차장 개수 조회 메서드 호출 검증
    verify(parkingZoneRepository).count();
    // ID 목록으로 주차장 조회 메서드 호출 검증
    verify(parkingZoneRepository).findAllByIdIn(
        eq(Arrays.asList(16L, 17L, 18L, 19L, 20L, 2L, 4L, 6L, 8L, 10L)));
    assertNotNull(response);
    assertEquals(1, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
  }

  @Test
  @DisplayName("favoriteZone이 0개 이면서 1페이지 요청이 잘 수행 되었는지")
  void testGetParkingZonesWithFavoriteZeroAndRequestPage1() {
    // given
    // 1. Mock favoriteZones 관련 데이터 설정 (favorite 주차장이 0개일 때)
    List<FavoriteParkingZoneEntity> favoriteZones = createFavoriteZones(0);
    AtomicInteger counter = new AtomicInteger(1);
    //  favoriteZones-parkingZone 엔티티 0개를 가지는 페이지 반환
    List<FavoriteParkingZoneEntity> updatedFavoriteZones = favoriteZones.stream()
        .peek(zone -> zone.getParkingZoneEntity().setId((long) counter.getAndAdd(1)))
        .collect(Collectors.toList());
    // PageImpl로 완성
    Page<FavoriteParkingZoneEntity> mockPage = new PageImpl<>(updatedFavoriteZones, pageable,
        favoriteZones.size());

    // when절을 통한 favoriteZones return
    when(
        favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(anyLong(),
            any(PageRequest.class)))
        .thenReturn(mockPage);

    // 총 주차장 개수 20개로 mocking
    when(parkingZoneRepository.count()).thenReturn(20L); // 전체 주차장 20개

    // Non-Favorite-ParkingZone Mock
    when(parkingZoneRepository.findClosestParkingZonesIDWithExclusion(
        anyDouble(), anyDouble(), anyInt(), anyInt(), anyList()))
        .thenReturn(
            new ArrayList<>(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)));

    //최종 반환 되는 주차장 ID 배열 (1 - 10)로 ID 값을 설정
    List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

    // 최종 반환 주차장 mocking
    // 1 ~ 10을 순차적으로 ID로 가지는 ParkingZoneEntity 반환
    List<ParkingZoneEntity> totalReturnedParkingZones = createNonFavoriteZones(10);
    AtomicInteger counter2 = new AtomicInteger(0);  // 시작값을 0으로 설정
    // ID 값을 순차적으로 설정하여 ParkingZoneEntity 객체에 적용
    List<ParkingZoneEntity> updatedParkingZones = totalReturnedParkingZones.stream()
        .peek(zone -> zone.setId(ids.get(counter2.getAndAdd(1))))  // 순차적으로 ID 할당
        .collect(Collectors.toList());

    when(parkingZoneRepository.findAllByIdIn(anyList()))
        .thenReturn(updatedParkingZones);

    // when
    // 서비스 메소드 호출
    ParkingZoneListResponse response = parkingZoneInfoService.getParkingZones(ParkingZoneListDTO
        .builder()
        .userId(1L)
        .parkingZoneListRequest(request)
        .build());

    // then
    // 즐겨찾기 주차장 조회 메서드 호출 검증
    verify(favoriteParkingZoneRepository).findAllByMemberEntity_IdOrderByIdAsc(eq(1L),
        eq(pageable));
    // 가까운 주차장 ID 조회 메서드 호출 검증
    verify(parkingZoneRepository).findClosestParkingZonesIDWithExclusion(
        eq(request.getLatitude()), eq(request.getLongitude()), eq(10), eq(0),
        eq(Arrays.asList(0L)));
    // 전체 주차장 개수 조회 메서드 호출 검증
    verify(parkingZoneRepository).count();
    // ID 목록으로 주차장 조회 메서드 호출 검증
    verify(parkingZoneRepository).findAllByIdIn(
        eq(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L)));
    assertNotNull(response);
    assertEquals(0, response.getPagination().getCurrentPage());
    assertEquals(20, response.getPagination().getTotalItems());
    assertEquals(10, response.getParkingZones().size()); // 즐겨찾기 10개
    assertEquals(2, response.getPagination().getTotalPages()); // 페이지 수 확인
  }

  @Test
  @DisplayName("favoriteZone이 0개 이면서 2페이지 요청이 잘 수행 되었는지")
  void testGetParkingZonesWithFavoriteZeroAndRequestPage2() {

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
