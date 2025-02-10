package org.example.honorsparkingbe.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.dto.DeleteParkingHistoryDTO;
import org.example.honorsparkingbe.dto.response.ParkingHistoryDeleteResponse;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ParkingHistoryService {

  private final ParkingHistoryRepository parkingHistoryRepository;

  /**
   * @param deleteParkingHistoryDTO
   * @return
   */
  @Transactional
  public ParkingHistoryDeleteResponse softDeleteParkingHistories(
      DeleteParkingHistoryDTO deleteParkingHistoryDTO) {
    // 1. 유저 정보 가져오기
    List<Long> targetIds = deleteParkingHistoryDTO.getParingHistoryDeleteRequest()
        .getHistoryIDList();
    Long userId = deleteParkingHistoryDTO.getUserId();

    // 2. 해당 ID에 대한 권한 확인
    List<ParkingHistoryEntity> userHistories = parkingHistoryRepository.findByIdsAndMember(
        targetIds, userId);
    // 본인의 이력이 맞는지 검증

    // 3. 본인 ID가 아닌 것은 필터링
    Set<Long> validIds = userHistories.stream()
        .map(ParkingHistoryEntity::getId)
        .collect(Collectors.toSet());

    List<Long> deletedIds = new ArrayList<>();
    List<Long> failedIds = new ArrayList<>();

    for (Long id : targetIds) {
      if (validIds.contains(id)) {
        deletedIds.add(id); // 삭제 성공 리스트에 추가
      } else {
        failedIds.add(id); // 삭제 실패 리스트에 추가
      }
    }

    // 4. soft delete sql수행
    parkingHistoryRepository.softDeleteAtByIds(LocalDateTime.now(), deletedIds);

    return ParkingHistoryDeleteResponse.builder()
        .isSuccess(targetIds.size() == deletedIds.size())
        .deletedIds(deletedIds)
        .failedIds(failedIds)
        .build();

  }
}
