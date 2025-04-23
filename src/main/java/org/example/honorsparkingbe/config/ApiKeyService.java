package org.example.honorsparkingbe.config;

import java.security.SecureRandom;
import java.util.Base64;
import org.example.honorsparkingbe.util.RedisUtil;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

  private final RedisUtil redisUtil;

  public ApiKeyService(RedisUtil redisUtil) {
    this.redisUtil = redisUtil;
  }

  // API Key 조회
  public String getApiKey() {
    return redisUtil.getApiKey();
  }

  // API Key 저장
  public void setApiKey(String newKey) {
    redisUtil.setApiKey(newKey);
  }

  // API Key Rotate
  public String rotateApiKey() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[32]; // 256비트 키
    secureRandom.nextBytes(randomBytes);

    String newKey = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    setApiKey(newKey); // 새로운 API Key Redis에 저장
    return newKey;
  }
}

