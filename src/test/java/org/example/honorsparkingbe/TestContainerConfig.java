//package org.example.honorsparkingbe;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.extension.BeforeAllCallback;
//import org.junit.jupiter.api.extension.ExtensionContext;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.utility.DockerImageName;
//
//@DisplayName("Redis Test Containers")
//@TestConfiguration
//public class TestContainerConfig implements BeforeAllCallback {
//
//  private static final String REDIS_IMAGE = "redis:7.0.8-alpine";
//  private static final int REDIS_PORT = 6379;
//  private GenericContainer redis;
//
//  @Override
//  public void beforeAll(ExtensionContext context) {
//    redis = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
//        .withExposedPorts(REDIS_PORT);
//    redis.start();
//    System.setProperty("spring.data.redis.host", redis.getHost());
//    System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(REDIS_PORT
//    )));
//  }
//}
