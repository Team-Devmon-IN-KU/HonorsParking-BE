package org.example.honorsparkingbe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.honorsparkingbe.dto.NotificationQueueItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
//@ConfigurationProperties(
//    prefix = "spring.data.redis"
//)
public class RedisConfig {

  @Value("${spring.data.redis.port}")
  public int port;

  @Value("${spring.data.redis.host}")
  public String host;

  @Value("${spring.data.redis.password}")
  private String password;


  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host, port);
    redisConfig.setPassword(password); // Redis 인증
    return new LettuceConnectionFactory(redisConfig);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    template.setKeySerializer(new StringRedisSerializer()); // Redis 키 직렬화
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 직렬화 사용
    // template.setValueSerializer(new JdkSerializationRedisSerializer()); // JDK 직렬화 사용.
    return template;
  }

  @Bean
  public RedisTemplate<String, NotificationQueueItem> notificationRedisTemplate(
      RedisConnectionFactory redisConnectionFactory
  ) {
    // ObjectMapper 직접 생성 (Bean으로 등록 X)
    BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
        .allowIfSubType(Object.class)
        .build();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.activateDefaultTyping(validator, ObjectMapper.DefaultTyping.NON_FINAL);

    RedisTemplate<String, NotificationQueueItem> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);
    template.setKeySerializer(new StringRedisSerializer());

    // 커스텀 ObjectMapper를 사용하는 Serializer 설정
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

    return template;
  }
}
