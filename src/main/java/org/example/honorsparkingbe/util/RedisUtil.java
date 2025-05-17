package org.example.honorsparkingbe.util;

import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private static final String QUEUE_KEY = "notification:queue";
  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisTemplate<String, NotificationQueueItem> notificationRedisTemplate;

  // Redis에 값 저장 (만료 시간 지정 가능)
  public void set(String key, Object value, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set(key, value, timeout, unit);
  }

  // 만료 시간 없이 Redis에 값 저장
  public void set(String key, Object value) {
    redisTemplate.opsForValue().set(key, value);
  }

  // Redis에서 값 조회
  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  // Redis에서 값 삭제
  public boolean delete(String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }

  // Redis에서 키 존재 여부 체크
  public boolean hasKey(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  // API Key를 Redis에 저장 (기본 메서드 활용)
  public void setApiKey(String newKey) {
    set("sync-server:api-key", newKey); // TTL 없이 저장
    // 또는 set("sync-server:api-key", newKey, 1, TimeUnit.DAYS); → TTL 줄 수도 있음
  }

  // API Key 조회 (기본 메서드 활용)
  public String getApiKey() {
    Object apiKey = get("sync-server:api-key");
    return apiKey != null ? apiKey.toString() : null;
  }
  // Notification 큐(notification:queue) 관련 기능
  // 1. 단일 객체 enqueue
  public void notiEnqueue(NotificationQueueItem value) {
    notificationRedisTemplate.opsForList().rightPush(QUEUE_KEY, value);
  }

  // 2. 여러 객체 한 번에 enqueue
  public void notiEnqueueAll(List<NotificationQueueItem> values) {
    notificationRedisTemplate.opsForList().rightPushAll(QUEUE_KEY, values);
  }

  // 3. 하나 dequeue (왼쪽 pop)
  public NotificationQueueItem notiDequeue() {
    return notificationRedisTemplate.opsForList().leftPop(QUEUE_KEY);
  }

  // 4. 큐 길이 확인 (선택)
  public Long notiQueueSize() {
    return notificationRedisTemplate.opsForList().size(QUEUE_KEY);
  }
}
