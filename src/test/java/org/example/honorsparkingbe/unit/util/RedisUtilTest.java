package org.example.honorsparkingbe.unit.util;

import org.example.honorsparkingbe.util.RedisUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:config/application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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

  @BeforeAll
  static void setUp() {
    System.setProperty("spring.data.redis.host", redis.getContainerIpAddress());
    System.setProperty("spring.data.redis.port", redis.getFirstMappedPort().toString());


  }

  @Autowired
  private RedisUtil redisUtil;

  @Test
  void sample() {
    // RedisUtil의 메서드를 호출
    redisUtil.get("ss");
  }

}
