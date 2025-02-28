package org.example.honorsparkingbe.util;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {

  private final RedisTemplate<String, Object> redisTemplate;

  // RedisTemplate을 사용하는 경우 RedisConnectionFactory를 설정하는 방법
  private final RedisConnectionFactory redisConnectionFactory;


  public void set(String key, Object value, long timeout, TimeUnit unit) {
    redisTemplate.opsForValue().set(key, value, timeout, unit);
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public boolean delete(String key) {
    return Boolean.TRUE.equals(redisTemplate.delete(key));
  }

  public boolean hasKey(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

}
