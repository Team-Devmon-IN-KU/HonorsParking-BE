package org.example.honorsparkingbe.config;

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

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
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
}
