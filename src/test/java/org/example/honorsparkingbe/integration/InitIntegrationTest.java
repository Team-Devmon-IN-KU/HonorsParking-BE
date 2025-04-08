package org.example.honorsparkingbe.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;


/**
 * Integration-Test전에 필요한 기본 환경세팅 1) docker test-container 2) 로그인 이후 세션 발금
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@TestPropertySource(locations = "classpath:application-integration-test.yml")
public class InitIntegrationTest {

  protected static final Logger logger = LoggerFactory.getLogger(InitIntegrationTest.class);

  // WebTestClient를 주입받아 HTTP 요청을 테스트할 수 있도록 설정
  @Autowired
  protected WebTestClient client;

  // Redis와 MySQL의 Docker 컨테이너 실행
  private static final String MYSQL_IMAGE = "mysql:latest";
  private static final String REDIS_IMAGE = "redis:latest";
  private static final int REDIS_PORT = 6379;

  @Container
  static public GenericContainer<?> redis = new GenericContainer<>(
      DockerImageName.parse(REDIS_IMAGE))
      .withExposedPorts(REDIS_PORT)
      .waitingFor(Wait.forListeningPort())
      .waitingFor(Wait.defaultWaitStrategy());

  @Container
  static public MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse(MYSQL_IMAGE))
      .waitingFor(Wait.forListeningPort());

  @BeforeAll
  static void setUp() {
    System.setProperty("spring.data.redis.host", redis.getHost());
    System.setProperty("spring.data.redis.port", redis.getFirstMappedPort().toString());
    System.setProperty("spring.datasource.url", mysql.getJdbcUrl());
    System.setProperty("spring.datasource.username", mysql.getUsername());
    System.setProperty("spring.datasource.password", mysql.getPassword());
    System.setProperty("api.key", "valid-api-key");
    System.setProperty("spring.jpa.hibernate.ddl-auto", "create");

    logger.info("MySQL URL : {}", mysql.getJdbcUrl());
  }

  @AfterAll
  static void tearDown() {
    System.clearProperty("spring.data.redis.host");
    System.clearProperty("spring.data.redis.port");
    System.clearProperty("spring.datasource.url");
    System.clearProperty("spring.datasource.username");
    System.clearProperty("spring.datasource.password");
  }

  protected static String sessionToken;

  /**
   * 실제 로그인 요청 이후 sessionToken에 유효한 세션 저장
   *
   * @param client
   * @param memberRepository
   * @param carRepository
   * @param passwordEncoder
   */
  @BeforeAll
  static void initTestUser(@Autowired WebTestClient client,
      @Autowired MemberRepository memberRepository,
      @Autowired CarRepository carRepository,
      @Autowired BCryptPasswordEncoder passwordEncoder) {

    CarEntity integrationCar = CarEntity.builder()
        .carNumber("integration-car")
        .carType(CarType.COMPACT)
        .isElectric(false).build();

    MemberEntity member = MemberEntity.builder()
        .authId("integration")
        .birthday("01-01")
        .birthdayYear(1990)
        .email("integration@example.com")
        .loginPlatform(LoginPlatform.NORMAL)
        .password(passwordEncoder.encode("integration"))
        .phoneNumber("01012341234")
        .role(MemberRole.ROLE_USER)
        .userName("name1")
        .carEntity(integrationCar)
        .build();

    carRepository.save(integrationCar);
    memberRepository.save(member);

    // 로그인 요청 후 세션 토큰 저장
    Map<String, String> loginRequest = Map.of(
        "username", "integration",
        "password", "integration"
    );

    client.post()
        .uri("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body(BodyInserters.fromFormData("username", loginRequest.get("username"))
            .with("password", loginRequest.get("password")))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .consumeWith(response -> {
          // 전체 쿠키 목록 출력
          Map<String, List<ResponseCookie>> cookies = response.getResponseCookies();
//          System.out.println("🔍 전체 쿠키 목록: " + cookies);

          // "SESSION" 쿠키에서 값을 가져옵니다.
          sessionToken = cookies.get("SESSION").stream()
              .findFirst()
              .map(ResponseCookie::getValue)  // 쿠키 값만 추출
              .orElse(null);

          // sessionToken이 null이 아닌지 검증
          assertThat(sessionToken)
              .as("로그인 후 세션 쿠키가 정상적으로 반환되어야 합니다.")
              .isNotNull();

        });
  }


}
