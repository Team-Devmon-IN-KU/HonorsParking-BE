package org.example.honorsparkingbe.unit.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.dto.DeleteParkingHistoryDTO;
import org.example.honorsparkingbe.dto.request.ParkingHistoryDeleteRequest;
import org.example.honorsparkingbe.dto.response.ParkingHistoryDeleteResponse;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ParkingHistoryDeleteServiceTest {

  @Mock
  private ParkingHistoryRepository parkingHistoryRepository;

  @InjectMocks
  private ParkingHistoryService parkingHistoryService;

  private MemberEntity testMember;

  private LocalDateTime firstHistoryTime;
  private LocalDateTime secondHistoryTime;
  private List<ParkingHistoryEntity> userParkingHistoryEntities;

  @BeforeEach
  void setUp() {
    firstHistoryTime = LocalDateTime.now();
    secondHistoryTime = firstHistoryTime.plusDays(1); // 같은 기준 시간에서 계산

    userParkingHistoryEntities = Arrays.asList(
        // 1번째 이력
        ParkingHistoryEntity.builder()
            .id(1L)
            .entranceTime(firstHistoryTime)
            .memberEntity(testMember)
            .build()
        ,
        ParkingHistoryEntity.builder()
            .id(2L)
            .entranceTime(secondHistoryTime)
            .memberEntity(testMember)
            .build()
    );
  }

  @Test
  public void 유저권환에_맞는_필터링_응답에반영되는지테스트() {
    // Given
    List<Long> targetIds = Arrays.asList(1L, 2L, 3L);
    Long userId = 100L; // Mock user ID

    // 레포지토리 모킹
    when(parkingHistoryRepository.findByIdsAndMember(targetIds, userId))
        .thenReturn(userParkingHistoryEntities);

    ParkingHistoryDeleteResponse response = parkingHistoryService.softDeleteParkingHistories(

        DeleteParkingHistoryDTO.builder()
            .userId(userId)
            .paringHistoryDeleteRequest(
                ParkingHistoryDeleteRequest.builder().historyIDList(targetIds).build())
            .build());

    assertEquals(true, response.getDeletedIds().contains(1L));
    assertEquals(true, response.getDeletedIds().contains(2L));
    assertEquals(true, response.getFailedIds().contains(3L));
    assertEquals(false, response.getIsSuccess());
  }
}
