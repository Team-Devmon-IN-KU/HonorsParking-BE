package org.example.honorsparkingbe.unit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.example.honorsparkingbe.domain.session.PaymentSessionManager;
import org.example.honorsparkingbe.util.RedisUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentSessionManagerTest {

  @Mock
  private RedisUtil redisUtil;

  @InjectMocks
  private PaymentSessionManager paymentSessionManager;

  private final Long userId = 123L;
  private final Long amount = 5000L;
  private final String sessionKey = "payment:session:" + userId;

  @Test
  @DisplayName("결제 세션 생성 성공 테스트")
  void createSession_ShouldStoreSessionInRedis() {
    // Given
    Map<String, Object> sessionData = new HashMap<>();
    sessionData.put("userId", userId);
    sessionData.put("amount", amount);
    sessionData.put("createdAt", System.currentTimeMillis());

    doNothing().when(redisUtil)
        .set(any(String.class), any(HashMap.class), any(Long.class), any(TimeUnit.class));
    // Mocking HttpServletResponse
    HttpServletResponse response = mock(HttpServletResponse.class);

    // When
    paymentSessionManager.createSession(userId, amount, response);

    // Then
    ArgumentCaptor<Map<String, Object>> sessionDataCaptor = ArgumentCaptor.forClass(Map.class);

    verify(redisUtil, times(1))
        .set(eq(sessionKey), sessionDataCaptor.capture(), eq(5L), eq(TimeUnit.MINUTES));

    Map<String, Object> capturedData = sessionDataCaptor.getValue();

    // 특정 값 검증
    assertEquals(userId, capturedData.get("userId"));
    assertEquals(amount, capturedData.get("amount"));
    assertTrue(capturedData.containsKey("createdAt")); // 존재 여부만 체크

    // Verify cookie is added to the response
    ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
    verify(response, times(1)).addCookie(cookieCaptor.capture());
  
    Cookie capturedCookie = cookieCaptor.getValue();
    assertEquals("paymentSession", capturedCookie.getName());
    assertEquals(sessionKey, capturedCookie.getValue());
    assertTrue(capturedCookie.getMaxAge() > 0);  // 쿠키의 만료 시간이 설정되어 있는지 확인
    assertTrue(capturedCookie.isHttpOnly());    // HttpOnly 속성 확인
  }


  @Test
  @DisplayName("존재하는 세션이 유효한지 검증하는 테스트")
  void isValidSession_ShouldValidInRedis() {
    // Given
    Long userId = 123L;
    String sessionKey = "payment:session:" + userId;

    // redisUtil의 hasKey 메서드를 모킹하여 세션이 존재한다고 가정
    when(redisUtil.hasKey(sessionKey)).thenReturn(true);

    // When
    boolean isValid = paymentSessionManager.isValidSession(userId);

    // Then
    assertTrue("세션이 유효해야 합니다.", isValid);
    // verify
    verify(redisUtil, times(1)).hasKey(sessionKey);
  }

  @Test
  @DisplayName("존재하지 않는 세션이 유효한지 검증하는 테스트")
  void isValidSession_ShouldNonValidInRedis() {
    // Given
    Long userId = 123L;
    String sessionKey = "payment:session:" + userId;

    // redisUtil의 hasKey 메서드를 모킹하여 세션이 존재하지 않는다고 가정
    when(redisUtil.hasKey(sessionKey)).thenReturn(false);

    // When
    boolean isValid = paymentSessionManager.isValidSession(userId);

    // Then
    assertFalse(isValid, "세션이 존재하지 않아야 합니다.");

    // 확인을 위한 verify
    verify(redisUtil, times(1)).hasKey(sessionKey);
  }

  @Test
  @DisplayName("삭제 호출시 redis유틸 동작하는지 확인하는 테스트")
  void deleteSession_ShouldDeleteInRedis() {
    // Given
    Long userId = 123L;
    String sessionKey = "payment:session:" + userId;

    // When
    paymentSessionManager.deleteSession(userId);

    // Then
    verify(redisUtil, times(1)).delete(sessionKey);
  }


}
