package org.example.honorsparkingbe.unit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.AddFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.DeleteFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.request.AddFavoriteParkingZoneRequest;
import org.example.honorsparkingbe.dto.request.DeleteFavoriteParkingZoneRequest;
import org.example.honorsparkingbe.dto.response.AddFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.dto.response.DeleteFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.repository.internal.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.service.FavoriteParkingZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FavoriteParkingZoneServiceTest {

  @Mock
  private FavoriteParkingZoneRepository favoriteParkingZoneRepository;

  @InjectMocks
  private FavoriteParkingZoneService favoriteParkingZoneService;

  @BeforeEach
  void setUp() {

  }

  @Test
  @DisplayName("즐겨찾기 주차 구역 추가 성공 시 응답이 올바르게 반환되어야 한다.")
  void addFavoriteParkingZone_shouldReturnSuccess_whenSaveSucceeds() {
    // Given
    AddFavoriteParkingZoneDTO dto = mock(AddFavoriteParkingZoneDTO.class);
    AddFavoriteParkingZoneRequest request = mock(AddFavoriteParkingZoneRequest.class);
    when(dto.getAddFavoriteParkingZoneRequest()).thenReturn(request);
    when(request.getParkingZoneId()).thenReturn(1L);
    when(dto.getUserId()).thenReturn(1L);

    // FavoriteParkingZoneEntity 생성
    FavoriteParkingZoneEntity favoriteEntity = FavoriteParkingZoneEntity.builder()
        .memberEntity(MemberEntity.builder().id(1L).build())
        .parkingZoneEntity(ParkingZoneEntity.builder().id(1L).build())
        .build();

    // When
    when(favoriteParkingZoneRepository.save(any(FavoriteParkingZoneEntity.class))).thenReturn(
        favoriteEntity);

    AddFavoriteParkingZoneResponse response = favoriteParkingZoneService.addFavoriteParkingZone(
        dto);

    // Then
    assertTrue(response.isSuccess());
    assertTrue(response.isBookmark());
    assertEquals(1L, response.getParkingZoneId().longValue());
    verify(favoriteParkingZoneRepository, times(1)).save(any(FavoriteParkingZoneEntity.class));
  }

//  @Test 에러 처리 방법 변경으로 인한 테스트 보류
//  @DisplayName("데이터 무결성 위반 발생 시 즐겨찾기 주차 구역 추가 실패 응답이 반환되어야 한다.")
//  void addFavoriteParkingZone_shouldReturnFailure_whenDataIntegrityViolationOccurs() {
//    // Given
//    AddFavoriteParkingZoneDTO dto = mock(AddFavoriteParkingZoneDTO.class);
//    AddFavoriteParkingZoneRequest request = mock(AddFavoriteParkingZoneRequest.class);
//    when(dto.getAddFavoriteParkingZoneRequest()).thenReturn(request);
//    when(request.getParkingZoneId()).thenReturn(1L);
//    when(dto.getUserId()).thenReturn(1L);
//
//    // When
//    when(favoriteParkingZoneRepository.save(any(FavoriteParkingZoneEntity.class)))
//        .thenThrow(new DataIntegrityViolationException("Data Integrity Violation"));
//
//    AddFavoriteParkingZoneResponse response = favoriteParkingZoneService.addFavoriteParkingZone(
//        dto);
//
//    // Then
//    assertFalse(response.isSuccess());
//    assertTrue(response.isBookmark());
//    assertEquals(1L, response.getParkingZoneId().longValue());
//    verify(favoriteParkingZoneRepository, times(1)).save(any(FavoriteParkingZoneEntity.class));
//  }

//  @Test 에러 처리 방법 변경으로 인한 테스트 보류
//  @DisplayName("예상치 못한 예외 발생 시 즐겨찾기 주차 구역 추가 실패 응답이 반환되어야 한다.")
//  void addFavoriteParkingZone_shouldReturnFailure_whenUnexpectedExceptionOccurs() {
//    // Given
//    AddFavoriteParkingZoneDTO dto = mock(AddFavoriteParkingZoneDTO.class);
//    AddFavoriteParkingZoneRequest request = mock(AddFavoriteParkingZoneRequest.class);
//    when(dto.getAddFavoriteParkingZoneRequest()).thenReturn(request);
//    when(request.getParkingZoneId()).thenReturn(1L);
//    when(dto.getUserId()).thenReturn(1L);
//
//    // When
//    when(favoriteParkingZoneRepository.save(any(FavoriteParkingZoneEntity.class)))
//        .thenThrow(new RuntimeException("Unexpected error"));
//
//    favoriteParkingZoneService.addFavoriteParkingZone(dto);
//
//    // Then
//    assertThat(assertThrows(RuntimeException.class, () ->
//        favoriteParkingZoneService.addFavoriteParkingZone(any(AddFavoriteParkingZoneDTO.class))
//    )).hasMessageContaining("[FavoriteParkingZoneService.toggleFavoriteParkingZone] 중복 데이터 저장 시도");
//  }

  @Test
  @DisplayName("즐겨찾기 주차 구역 삭제 성공 시 응답이 올바르게 반환되어야 한다.")
  void testDeleteFavoriteParkingZone_Success() {
    // Given
    DeleteFavoriteParkingZoneDTO dto = mock(DeleteFavoriteParkingZoneDTO.class);
    DeleteFavoriteParkingZoneRequest request = mock(DeleteFavoriteParkingZoneRequest.class);
    when(dto.getDeleteFavoriteParkingZoneRequest()).thenReturn(request);
    when(request.getParkingZoneId()).thenReturn(1L);
    when(dto.getUserId()).thenReturn(1L);

    //When
    when(favoriteParkingZoneRepository.deleteByMemberEntity_IdAndParkingZoneEntity_Id(
        any(Long.class), any(Long.class))).thenReturn(1);

    DeleteFavoriteParkingZoneResponse response = favoriteParkingZoneService
        .deleteFavoriteParkingZone(dto);

    // Then
    assertNotNull(response);
    assertTrue(response.isSuccess());
    assertFalse(response.isBookmark());
    assertEquals(1L, response.getParkingZoneId().longValue());
    verify(favoriteParkingZoneRepository, times(1))
        .deleteByMemberEntity_IdAndParkingZoneEntity_Id(any(Long.class), any(Long.class));
  }

  @Test
  @DisplayName("즐겨찾기 주차 구역 삭제 시 예상치 못한 에러 발생시 응답이 올바르게 반환되어야 한다.")
  void testDeleteFavoriteParkingZone_Failure() {
    // Given
    DeleteFavoriteParkingZoneDTO dto = mock(DeleteFavoriteParkingZoneDTO.class);
    DeleteFavoriteParkingZoneRequest request = mock(DeleteFavoriteParkingZoneRequest.class);
    when(dto.getDeleteFavoriteParkingZoneRequest()).thenReturn(request);
    when(request.getParkingZoneId()).thenReturn(1L);
    when(dto.getUserId()).thenReturn(1L);

    // When
    // 삭제 레포지토리 메서드 호출 이후 0개가 삭제됨 (존재하지 않는 즐겨찾기 구역)
    when(favoriteParkingZoneRepository.deleteByMemberEntity_IdAndParkingZoneEntity_Id(
        any(Long.class), any(Long.class))).thenReturn(0);  // 0개가 삭제됨

    // Then
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      favoriteParkingZoneService.deleteFavoriteParkingZone(dto);
    });

    // 예외 메시지가 예상대로 출력되는지 확인
    assertEquals(
        "[FavoriteParkingZoneService-deleteFavoriteParkingZone] 에러 발생, memberId: 1, parkingZoneId: 1",
        exception.getMessage()
    );
  }

}
