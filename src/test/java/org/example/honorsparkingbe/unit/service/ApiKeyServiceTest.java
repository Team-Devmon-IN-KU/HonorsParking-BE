package org.example.honorsparkingbe.unit.service;

import java.lang.reflect.Method;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.example.honorsparkingbe.config.ApiKeyService;
import org.example.honorsparkingbe.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ApiKeyServiceTest {

  @Mock
  private RedisUtil redisUtil;

  @InjectMocks
  private ApiKeyService apiKeyService;

  @Mock
  private HttpClient httpClient; // HttpClient Mocking

  @Test
  void rotateApiKey_ShouldGenerateAndStoreNewKey() {
    // when
    String newKey = apiKeyService.rotateApiKey();

    // then
    System.out.println("▶ 새로 생성된 API Key: " + newKey);

    assertNotNull(newKey);
    assertTrue(newKey.matches("^[A-Za-z0-9_-]+$"));

    // redisUtil.setApiKey(newKey)가 실제 호출되었는지 확인
    verify(redisUtil).setApiKey(newKey);
  }

  @Test
  void getApiKey_ShouldReturnStoredKey() {
    // given
    String expectedKey = "test-key";
    when(redisUtil.getApiKey()).thenReturn(expectedKey);

    // when
    String actualKey = apiKeyService.getApiKey();

    // then
    System.out.println("Redis에서 가져온 API Key: " + actualKey);
    assertEquals(expectedKey, actualKey);
  }

  @Test
  void setApiKey_ShouldStoreKeyInRedis() {
    // given
    String keyToStore = "store-this-key";

    // when
    apiKeyService.setApiKey(keyToStore);

    // then
    System.out.println("Redis에 저장한 API Key: " + keyToStore);
    verify(redisUtil).setApiKey(keyToStore);
  }


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    httpClient = mock(HttpClient.class);  // HttpClient를 Mock 생성
    redisUtil = mock(RedisUtil.class);    // RedisUtil도 Mock

    apiKeyService = new ApiKeyService(redisUtil, httpClient); // 수정된 생성자에 맞게
    apiKeyService = Mockito.spy(apiKeyService);  // 필요하면 Spy
  }


  @Test
  void testSendNewApiKeyToSyncServer() throws Exception {
    // Arrange
    String newApiKey = "new-api-key-1234";

    HttpResponse<String> mockResponse = mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);

    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    Method method = ApiKeyService.class.getDeclaredMethod("sendNewApiKeyToSyncServer",
        String.class);
    method.setAccessible(true);

    // 실제 메서드 호출
    method.invoke(apiKeyService, newApiKey);

    verify(httpClient, times(1))
        .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
  }

  @Test
  void testRotateApiKeyAndSendToSyncServer() throws Exception {
    //given
    HttpResponse<String> mockResponse = mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);

    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    //WHEN
    String rotatedKey = apiKeyService.rotateApiKey();

    //THEN
    assertNotNull(rotatedKey);
    assertEquals(43, rotatedKey.length());
    System.out.println("Rotated Key: " + rotatedKey);

    verify(httpClient, times(1)).send(any(HttpRequest.class),
        eq(HttpResponse.BodyHandlers.ofString()));
  }
}



