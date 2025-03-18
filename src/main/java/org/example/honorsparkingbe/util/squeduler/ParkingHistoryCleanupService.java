package org.example.honorsparkingbe.util.squeduler;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingHistoryCleanupService {

  private final ParkingHistoryRepository parkingHistoryRepository;

  @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정 실행
  @Transactional
  public void deleteExpiredParkingHistories() {
    LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
    parkingHistoryRepository.deleteAllByDeleteAtBefore(oneMonthAgo);
  }
}
