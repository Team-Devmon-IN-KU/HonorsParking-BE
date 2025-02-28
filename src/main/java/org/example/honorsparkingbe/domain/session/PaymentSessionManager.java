package org.example.honorsparkingbe.domain.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.util.RedisUtil;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSessionManager {

  private final RedisUtil redisUtil;
  private static final long SESSION_TIMEOUT = 5;

  public void createSession(Long userId, Long amount) {
    String sessionKey = getSessionKey(userId);
    Map<String, Object> sessionData = new HashMap<>();
    sessionData.put("userId", userId);
    sessionData.put("amount", amount);
    sessionData.put("createdAt", System.currentTimeMillis());

    redisUtil.set(sessionKey, sessionData, SESSION_TIMEOUT, TimeUnit.MINUTES);
  }

  public Map<String, Object> getSession(Long userId) {
    String sessionKey = getSessionKey(userId);
    Object session = redisUtil.get(sessionKey);
    if (session == null) {
      throw new RuntimeException("결제 세션이 존재하지 않습니다.");
    }
    return (Map<String, Object>) session;
  }

  public boolean isValidSession(Long userId) {
    return redisUtil.hasKey(getSessionKey(userId));
  }

  public void deleteSession(Long userId) {
    redisUtil.delete(getSessionKey(userId));
  }

  private String getSessionKey(Long userId) {
    return "payment:session:" + userId;
  }
}
