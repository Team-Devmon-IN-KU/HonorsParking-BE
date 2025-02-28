package org.example.honorsparkingbe.domain.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
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

  /**
   * 결제 세션을 만들어 Redis에 저장하는 메서드
   *
   * @param userId 결제 세션을 만들 UserID
   * @param amount
   */
  public void createSession(Long userId, Long amount, HttpServletResponse response) {
    String sessionKey = getSessionKey(userId);
    Map<String, Object> sessionData = new HashMap<>();
    sessionData.put("userId", userId);
    sessionData.put("amount", amount);
    sessionData.put("createdAt", System.currentTimeMillis());

    redisUtil.set(sessionKey, sessionData, SESSION_TIMEOUT, TimeUnit.MINUTES);

    // 쿠키 생성
    Cookie sessionCookie = new Cookie("paymentSession", sessionKey);  // 세션 쿠키 생성
    sessionCookie.setHttpOnly(true);  // 보안을 위해 HttpOnly 설정
    sessionCookie.setMaxAge((int) (amount * SESSION_TIMEOUT));  // 최대 5분 동안 유효
    sessionCookie.setPath("/");  // 모든 경로에서 유효

    // 응답에 쿠키 추가
    response.addCookie(sessionCookie);
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
