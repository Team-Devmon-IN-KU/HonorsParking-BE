package org.example.honorsparkingbe.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyInitializer {

  private final ApiKeyService apiKeyService;

  public ApiKeyInitializer(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @PostConstruct
  public void init() {
    if (apiKeyService.getApiKey() == null) {
      apiKeyService.rotateApiKey();
      System.out.println("API Key 초기화 완료");
    }
  }
}

