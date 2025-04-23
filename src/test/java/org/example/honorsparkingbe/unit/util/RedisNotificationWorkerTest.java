package org.example.honorsparkingbe.unit.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.example.honorsparkingbe.domain.enums.NotiChannel;
import org.example.honorsparkingbe.domain.enums.NotiEventType;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.example.honorsparkingbe.service.KakaoNotificationTalkService;
import org.example.honorsparkingbe.util.RedisUtil;
import org.example.honorsparkingbe.util.squeduler.RedisNotificationWorker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RedisNotificationWorkerTest {

  @InjectMocks
  private RedisNotificationWorker worker;

  @Mock
  private RedisUtil redisUtil;

  @Mock
  private KakaoNotificationTalkService kakaoNotificationTalkService;

  private final NotificationQueueItem sampleEntryItem = NotificationQueueItem.builder()
      .notiChannel(NotiChannel.KAKAO)
      .notiEventType(NotiEventType.ENTRY)
      .phoneNumber("01012345678")
      .carNumber("12가3456")
      .entranceTime(LocalDateTime.now())
      .retryCount(0)
      .build();

  private final NotificationQueueItem sampleExitItem = NotificationQueueItem.builder()
      .notiChannel(NotiChannel.KAKAO)
      .notiEventType(NotiEventType.EXIT)
      .phoneNumber("01012345678")
      .carNumber("12가3456")
      .entranceTime(LocalDateTime.now())
      .retryCount(0)
      .build();

  @Test
  @DisplayName("입차 알람과 카카오알람 형태일 때 올바른 서비스 호출 검증 테스트")
  void pollQueue_shouldProcessEntryNotification() {
    when(redisUtil.notiQueueSize()).thenReturn(1L);
    when(redisUtil.notiDequeue()).thenReturn(sampleEntryItem);

    worker.pollQueue();

    verify(kakaoNotificationTalkService).sendcarEntryAlarm(
        sampleEntryItem.getPhoneNumber(),
        sampleEntryItem.getCarNumber(),
        sampleEntryItem.getEntranceTime()
    );
  }

  @Test
  @DisplayName("출차 알람과 카카오알람 형태일 때 올바른 서비스 호출 검증 테스트")
  void pollQueue_shouldProcessExitNotification() {
    when(redisUtil.notiQueueSize()).thenReturn(1L);
    when(redisUtil.notiDequeue()).thenReturn(sampleExitItem);

    worker.pollQueue();

    verify(kakaoNotificationTalkService).sendCarExitAlarm(
        sampleExitItem.getPhoneNumber(),
        sampleExitItem.getEntranceTime()
    );
  }

  @Test
  @DisplayName("")
  void pollQueue_shouldRetryOnFailure() {
    sampleEntryItem.setRetryCount(1);

    when(redisUtil.notiQueueSize()).thenReturn(1L);
    when(redisUtil.notiDequeue()).thenReturn(sampleEntryItem);
    doThrow(new RuntimeException("Failure")).when(kakaoNotificationTalkService)
        .sendcarEntryAlarm(any(), any(), any());

    worker.pollQueue();

    verify(redisUtil).notiEnqueue(argThat(item -> item.getRetryCount() == 2));
  }

  @Test
  void pollQueue_shouldGiveUpAfterThreeFailures() {
    sampleEntryItem.setRetryCount(3);

    when(redisUtil.notiQueueSize()).thenReturn(1L);
    when(redisUtil.notiDequeue()).thenReturn(sampleEntryItem);
    doThrow(new RuntimeException("Failure")).when(kakaoNotificationTalkService)
        .sendcarEntryAlarm(any(), any(), any());

    worker.pollQueue();

    verify(redisUtil, never()).notiEnqueue(any());
  }

  @Test
  void pollQueue_shouldProcessMultipleNotificationsUpToBatchSize() {
    // given
    NotificationQueueItem newItem = NotificationQueueItem.builder()
        .notiChannel(NotiChannel.KAKAO)
        .notiEventType(NotiEventType.ENTRY)
        .phoneNumber("01033334444")
        .carNumber("33가3333")
        .entranceTime(LocalDateTime.now())
        .retryCount(0)
        .build();

    when(redisUtil.notiQueueSize()).thenReturn(3L); // queue에 10개 있다고 가정
    when(redisUtil.notiDequeue())
        .thenReturn(sampleEntryItem)
        .thenReturn(sampleExitItem)
        .thenReturn(newItem);

    // when
    worker.pollQueue();

    // then
    verify(kakaoNotificationTalkService).sendcarEntryAlarm(
        sampleEntryItem.getPhoneNumber(), sampleEntryItem.getCarNumber(),
        sampleEntryItem.getEntranceTime());
    verify(kakaoNotificationTalkService).sendCarExitAlarm(
        sampleExitItem.getPhoneNumber(), sampleExitItem.getEntranceTime());
    verify(kakaoNotificationTalkService).sendcarEntryAlarm(
        newItem.getPhoneNumber(), newItem.getCarNumber(), newItem.getEntranceTime());

    // 알람 3개만 처리되었는지 검증 (BATCH_SIZE를 넘지 않도록)
    verify(redisUtil, times(3)).notiDequeue();
  }
}
