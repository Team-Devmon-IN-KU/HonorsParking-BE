package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.dto.AddFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.DeleteFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.response.AddFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.dto.response.DeleteFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.repository.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.MemberRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteParkingZoneService {

  private Logger logger = LoggerFactory.getLogger(FavoriteParkingZoneService.class);

  private final FavoriteParkingZoneRepository favoriteParkingZoneRepository;
  private final ParkingZoneRepository parkingZoneRepository;
  private final MemberRepository memberRepository;

  public AddFavoriteParkingZoneResponse addFavoriteParkingZone(
      AddFavoriteParkingZoneDTO dto) {

    Long userId = dto.getUserId();
    Long parkingZoneId = dto.getAddFavoriteParkingZoneRequest().getParkingZoneId();

    // 중복 여부 확인 없이 바로 저장 (DB에 Unique 제약 조건 설정)
    FavoriteParkingZoneEntity newFavorite = FavoriteParkingZoneEntity.builder()
        .memberEntity(MemberEntity.builder().id(userId).build()) // Proxy 사용으로 쿼리 줄임
        .parkingZoneEntity(ParkingZoneEntity.builder().id(parkingZoneId).build()) // Proxy 사용
        .build();

    try {
      favoriteParkingZoneRepository.save(newFavorite);
      return AddFavoriteParkingZoneResponse.builder()
          .isSuccess(true)
          .isBookmark(true)
          .parkingZoneId(parkingZoneId)
          .build();
    } catch (DataIntegrityViolationException e) { // 이미 존재하는 경우 실패 처리
      logger.warn(
          "[FavoriteParkingZoneService.toggleFavoriteParkingZone] 중복 데이터 저장 시도 또는 제약 조건 위반 - userId: {}, parkingZoneId: {}, error: {}",
          userId, parkingZoneId, e.getMessage());

      return AddFavoriteParkingZoneResponse.builder()
          .isSuccess(false)
          .isBookmark(true)
          .parkingZoneId(parkingZoneId)
          .build();
    } catch (Exception e) {
      logger.error(
          "[FavoriteParkingZoneService.toggleFavoriteParkingZone] 예상치 못한 오류 발생 - userId: {}, parkingZoneId: {} error: {}",
          userId, parkingZoneId, e.getMessage());

      return AddFavoriteParkingZoneResponse.builder()
          .isSuccess(false)
          .isBookmark(false)
          .parkingZoneId(parkingZoneId)
          .build();
    }
  }

  @Transactional
  public DeleteFavoriteParkingZoneResponse deleteFavoriteParkingZone(
      DeleteFavoriteParkingZoneDTO dto
  ) {
    Long userId = dto.getUserId();
    Long parkingZoneId = dto.getDeleteFavoriteParkingZoneRequest().getParkingZoneId();

    int deleteCount = favoriteParkingZoneRepository.deleteByMemberEntity_IdAndParkingZoneEntity_Id(
        userId,
        parkingZoneId);

    if (deleteCount != 1) {
      throw new RuntimeException(String.format(
          "[FavoriteParkingZoneService-deleteFavoriteParkingZone] 에러 발생, memberId: %d, parkingZoneId: %d",
          userId, parkingZoneId)
      );
    }

    return DeleteFavoriteParkingZoneResponse.builder()
        .isSuccess(true)
        .isBookmark(false)
        .parkingZoneId(parkingZoneId)
        .build();
  }
}
