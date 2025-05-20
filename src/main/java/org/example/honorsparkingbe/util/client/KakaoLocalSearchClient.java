package org.example.honorsparkingbe.util.client;

import java.net.URI;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse;
import org.example.honorsparkingbe.service.FavoriteParkingZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
public class KakaoLocalSearchClient {

  private final String baseUrl;
  private final String authorizationKey;
  private final WebClient webClient;

  public KakaoLocalSearchClient(
      @Value("${kakao.api.key}") String kakaoApiKey,
      @Value("${kakao.api.base-url}") String baseUrl
  ) {
    this.baseUrl = baseUrl;
    this.authorizationKey = "KakaoAK " + kakaoApiKey;
    this.webClient = WebClient.builder()
        .baseUrl(baseUrl)
        .defaultHeader("Authorization", this.authorizationKey)
        .build();
  }

  static String SEARCH_PATH = "search/keyword";
  static String AuthorizationHeader = "AuthorizationHeader";
  private final Logger logger = LoggerFactory.getLogger(FavoriteParkingZoneService.class);

  /**
   * 카카오 페이징은 1 베이스 넘버링이기 때문에 1이상을 줘야함
   *
   * @return
   */
  public KakaoLocalClientResponse getLoocalsByKeyword(SearchLocalDTO dto) {
    URI uri = UriComponentsBuilder.fromUriString(baseUrl)
        .path(SEARCH_PATH)
        .queryParam("query", dto.getKeyword())
        .build(false) // 아직 encode 하지 않음
        .toUri();

    UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);

    if (dto.getLongitudeX() != null && dto.getLatitudeY() != null) {
      builder.queryParam("x", dto.getLongitudeX());
      builder.queryParam("y", dto.getLatitudeY());
    }

    if (dto.getPage() != null) {
      builder.queryParam("page", dto.getPage() + 1); // Kakao API는 1-based
    }

    URI finalUri = builder.build(true).toUri(); // 마지막에 encode 처리

    logger.info("Kakao Local Search URI: {}", finalUri);

    return webClient.get()
        .uri(uriBuilder -> finalUri) // 직접 만든 URI를 사용
        .header(AuthorizationHeader, authorizationKey)
        .retrieve()
        .onStatus(
            status -> status.is4xxClientError() || status.is5xxServerError(),
            clientResponse -> clientResponse
                .bodyToMono(String.class)
                .flatMap(errorBody -> {
                  logger.error("Kakao API Error: {}", errorBody);
                  return Mono.error(new RuntimeException("Kakao API 호출 실패: " + errorBody));
                })
        )
        .bodyToMono(KakaoLocalClientResponse.class)
        .block(); // 동기 처리
  }
}
