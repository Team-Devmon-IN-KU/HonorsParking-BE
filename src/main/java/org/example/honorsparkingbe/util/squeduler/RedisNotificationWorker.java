package org.example.honorsparkingbe.util.squeduler;

import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.example.honorsparkingbe.service.KakaoNotificationTalkService;
import org.example.honorsparkingbe.util.RedisUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisNotificationWorker {

  private final RedisUtil redisUtil;
  private final KakaoNotificationTalkService kakaoNotificationTalkService;
  private final Logger logger = Logger.getLogger(RedisNotificationWorker.class.getName());

  static Integer BATCH_SIZE = 1000;

  @Scheduled(fixedRate = 5000)
  public void pollQueue() {
    long queueSize = redisUtil.notiQueueSize();

    for (int i = 0; i < Math.min(queueSize, BATCH_SIZE); i++) {
      NotificationQueueItem item = redisUtil.notiDequeue();
      try {
        handleNotification(item);
      } catch (Exception e) {
        handleFailure(item, e);
      }
    }
  }

  private void handleNotification(NotificationQueueItem item) {
    switch (item.getNotiChannel()) {
      case KAKAO -> handleKakaoNotification(item);
      case PUSH -> handlePushNotification(item);
      default -> throw new IllegalArgumentException("지원하지 않는 알림 채널: " + item.getNotiChannel());
    }
  }

  private void handleKakaoNotification(NotificationQueueItem item) {
    switch (item.getNotiEventType()) {
      case ENTRY -> kakaoNotificationTalkService.sendcarEntryAlarm(
          item.getPhoneNumber(), item.getCarNumber(), item.getEntranceTime());

      case EXIT -> kakaoNotificationTalkService.sendCarExitAlarm(
          item.getPhoneNumber(), item.getEntranceTime());

      default ->
          throw new IllegalArgumentException("지원하지 않는 알림 이벤트 타입: " + item.getNotiEventType());
    }
  }

  private void handlePushNotification(NotificationQueueItem item) {
    // TODO: PUSH 알람 전송 로직
    logger.info("푸시 알람 처리 로직 추가 필요: " + item);
  }

  private void handleFailure(NotificationQueueItem item, Exception e) {
    int retryCount = item.getRetryCount() == null ? 0 : item.getRetryCount();

    if (retryCount < 3) {
      item.setRetryCount(retryCount + 1);
      redisUtil.notiEnqueue(item);
      logger.warning(String.format("알람 재시도 (%d회): %s", retryCount + 1, item));
    } else {
      logger.warning("알람 전송 3회 실패, 알람 포기");
      logger.warning("실패 알람 정보: " + item);
      logger.warning("예외 메시지: " + e.getMessage());
    }
  }

}
