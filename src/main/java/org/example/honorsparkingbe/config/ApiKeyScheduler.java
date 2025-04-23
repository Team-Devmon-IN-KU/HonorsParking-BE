package org.example.honorsparkingbe.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyScheduler {

  private final ApiKeyService apiKeyService;

  public ApiKeyScheduler(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  // 매달 1일 자정에 API Key 자동 갱신
  @Scheduled(cron = "0 0 0 1 * *")
  public void rotateApiKeyMonthly() {
    String newKey = apiKeyService.rotateApiKey();
    System.out.println("[API Key 자동 갱신] 새로운 키: " + newKey);
    // 필요하면 로그 파일로 남기거나 알림 전송 가능
  }
}


