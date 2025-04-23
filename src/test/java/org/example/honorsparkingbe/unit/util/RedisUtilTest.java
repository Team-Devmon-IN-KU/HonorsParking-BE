package org.example.honorsparkingbe.unit.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.example.honorsparkingbe.domain.enums.NotiChannel;
import org.example.honorsparkingbe.domain.enums.NotiEventType;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.example.honorsparkingbe.util.RedisUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@Transactional
/**
 * RedisUtilTest : 해당 테스트는 가상의 도커컨테이너에서 레디스를 띄우면서 실제 RW연산을 진행하기 때문에
 *                 SpringBootTest이용
 */
public class RedisUtilTest {

  private static final String REDIS_IMAGE = "redis:latest";
  private static final int REDIS_PORT = 6379;

  @Autowired
  private RedisTemplate<String, NotificationQueueItem> forVerifyRedisTemplate;

  @Container
  @ServiceConnection
  static public GenericContainer<?> redis = new GenericContainer<>(
      DockerImageName.parse(REDIS_IMAGE))
      .withExposedPorts(REDIS_PORT)
      .waitingFor(Wait.forListeningPort())
      .waitingFor(Wait.defaultWaitStrategy());

  @Autowired
  private RedisUtil redisUtil;

  @BeforeAll
  static void setUp() {
    System.setProperty("spring.data.redis.host", redis.getHost());
    System.setProperty("spring.data.redis.port", redis.getFirstMappedPort().toString());
  }

  @AfterEach
  void clearRedis() {
    redisUtil.delete("testKey");
    redisUtil.delete("existingKey");
    redisUtil.delete("nonExistingKey");
  }

  @Test
  @DisplayName("Redis Util Get Set 테트트")
  void testSetAndGet() {
    // Given
    String key = "testKey";
    String value = "testValue";
    long timeout = 10L;
    TimeUnit unit = TimeUnit.SECONDS;

    // When
    redisUtil.set(key, value, timeout, unit);
    Object retrievedValue = redisUtil.get(key);

    // Then
    assertNotNull(retrievedValue);
    assertEquals(value, retrievedValue);
  }

  @Test
  void testDelete() {
    // Given
    String key = "testKey";
    redisUtil.set(key, "toBeDeleted", 10, TimeUnit.SECONDS);

    // When
    boolean deleted = redisUtil.delete(key);

    // Then
    assertTrue(deleted);
    assertNull(redisUtil.get(key));
  }

  @Test
  void testHasKey() {
    // Given
    String key = "existingKey";
    redisUtil.set(key, "someValue", 10, TimeUnit.SECONDS);

    // When
    boolean exists = redisUtil.hasKey(key);
    boolean notExists = redisUtil.hasKey("nonExistingKey");

    // Then
    assertTrue(exists);
    assertFalse(notExists);
  }

  @Test
  @DisplayName("Redis Notification Queue enqueue One 테스트")
  void testNotificationQueueEnqueueOne() {
    // Given
    Long BeforeSize = forVerifyRedisTemplate.opsForList().size("notification:queue");

    // When
    redisUtil.notiEnqueue(
        NotificationQueueItem.builder()
            .phoneNumber("01012341234")
            .carNumber("123가1234")
            .notiEventType(NotiEventType.ENTRY)
            .notiChannel(NotiChannel.KAKAO)
            .entranceTime(LocalDateTime.now())
            .build()
    );

    // Then
    Long AfterSize = forVerifyRedisTemplate.opsForList().size("notification:queue");
    assertNotNull(BeforeSize);
    assertNotNull(AfterSize);
    assertEquals(1, AfterSize - BeforeSize);
  }

  @Test
  @DisplayName("Redis Notification Queue enqueue All테스트")
  void testNotificationQueueEnqueueAll() {
    // Given
    Long BeforeSize = forVerifyRedisTemplate.opsForList().size("notification:queue");

    NotificationQueueItem item1 = NotificationQueueItem.builder()
        .phoneNumber("01012341234")
        .carNumber("123가1234")
        .notiEventType(NotiEventType.ENTRY)
        .notiChannel(NotiChannel.KAKAO)
        .entranceTime(LocalDateTime.now())
        .build();

    NotificationQueueItem item2 = NotificationQueueItem.builder()
        .phoneNumber("01012341234")
        .carNumber("123가1234")
        .notiEventType(NotiEventType.ENTRY)
        .notiChannel(NotiChannel.KAKAO)
        .entranceTime(LocalDateTime.now())
        .build();

    NotificationQueueItem item3 = NotificationQueueItem.builder()
        .phoneNumber("01012341234")
        .carNumber("123가1234")
        .notiEventType(NotiEventType.ENTRY)
        .notiChannel(NotiChannel.KAKAO)
        .entranceTime(LocalDateTime.now())
        .build();

    List<NotificationQueueItem> items = List.of(item1, item2, item3);

    // When
    redisUtil.notiEnqueueAll(items);

    // Then
    Long AfterSize = forVerifyRedisTemplate.opsForList().size("notification:queue");
    assertNotNull(BeforeSize);
    assertNotNull(AfterSize);
    assertEquals(3, AfterSize - BeforeSize);
  }

  @Test
  @DisplayName("Redis Notification Queue enqueue All테스트")
  void testNotificationQueueDequeue() {
    // Given
    Long BeforeSize = forVerifyRedisTemplate.opsForList().size("notification:queue");

    NotificationQueueItem item = NotificationQueueItem.builder()
        .phoneNumber("01033333333")
        .carNumber("333가3333")
        .notiEventType(NotiEventType.ENTRY)
        .notiChannel(NotiChannel.KAKAO)
        .entranceTime(LocalDateTime.now())
        .build();
    forVerifyRedisTemplate.opsForList().rightPush("notification:queue", item);

    // When
    NotificationQueueItem dequeuedItem = redisUtil.notiDequeue();

    //Then
    assertNotNull(dequeuedItem);
    assertEquals(dequeuedItem.getPhoneNumber(), item.getPhoneNumber());
    assertEquals(dequeuedItem.getCarNumber(), item.getCarNumber());
    assertEquals(dequeuedItem.getNotiEventType(), item.getNotiEventType());
    assertEquals(dequeuedItem.getNotiChannel(), item.getNotiChannel());
    assertNotNull(BeforeSize);
    assertEquals(0, BeforeSize);
  }

  @Test
  @DisplayName("Redis Notification Queue enqueue get Size 테스트")
  void testNotificationQueueGetSize() {
    //Given
    Long BeforeSize = redisUtil.notiQueueSize();

    NotificationQueueItem item = NotificationQueueItem.builder()
        .phoneNumber("01033333333")
        .carNumber("333가3333")
        .notiEventType(NotiEventType.ENTRY)
        .notiChannel(NotiChannel.KAKAO)
        .entranceTime(LocalDateTime.now())
        .build();
    forVerifyRedisTemplate.opsForList()
        .rightPushAll("notification:queue", List.of(item, item, item, item));

    //When
    Long AfterSize = redisUtil.notiQueueSize();

    //Then
    assertNotNull(BeforeSize);
    assertEquals(0, BeforeSize);
    assertNotNull(AfterSize);
    assertEquals(4, AfterSize);

  }

}
