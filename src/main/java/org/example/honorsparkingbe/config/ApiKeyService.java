package org.example.honorsparkingbe.config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.Base64;
import org.example.honorsparkingbe.util.RedisUtil;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

  private final RedisUtil redisUtil;
  private final HttpClient httpClient;
  private final String syncServerUrl = "http://localhost:8080/api/v1/sync/inout";
  private static final String API_KEY_HEADER_NAME = "API-KEY-HEADER";

  public ApiKeyService(RedisUtil redisUtil, HttpClient httpClient) {
    this.redisUtil = redisUtil;
    this.httpClient = httpClient;
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
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);

    String newKey = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

    setApiKey(newKey);

    sendNewApiKeyToSyncServer(newKey);

    return newKey;
  }

  // 동기화 서버에 API 키 전송
  private void sendNewApiKeyToSyncServer(String newApiKey) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(syncServerUrl))
          .header(API_KEY_HEADER_NAME, newApiKey)
          .POST(HttpRequest.BodyPublishers.noBody())
          .build();

      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        System.out.println(" 동기화 서버로 API 키 전송 성공!");
      } else {
        System.err.println(" 동기화 서버로 API 키 전송 실패. 상태 코드: " + response.statusCode());
      }
    } catch (Exception e) {
      System.err.println(" 동기화 서버로 API 키 전송 중 에러 발생");
      e.printStackTrace();
    }
  }
}
