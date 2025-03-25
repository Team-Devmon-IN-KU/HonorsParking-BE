package org.example.honorsparkingbe.unit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import org.example.honorsparkingbe.util.RedisUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
/**
 * RedisUtilTest : 해당 테스트는 가상의 도커컨테이너에서 레디스를 띄우면서 실제 RW연산을 진행하기 때문에
 *                 SpringBootTest이용
 */
public class RedisUtilTest {

  private static final String REDIS_IMAGE = "redis:latest";
  private static final int REDIS_PORT = 6379;

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
    String host = redis.getHost();
    String port = redis.getFirstMappedPort().toString();

    System.out.println("✅ Redis Host: " + host);
    System.out.println("✅ Redis Port: " + port);

    System.setProperty("spring.data.redis.host", host);
    System.setProperty("spring.data.redis.port", port);
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

}
