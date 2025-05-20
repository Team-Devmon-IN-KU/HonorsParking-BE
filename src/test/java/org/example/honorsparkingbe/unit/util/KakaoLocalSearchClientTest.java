package org.example.honorsparkingbe.unit.util;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse.Document;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse.Meta;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse.SameName;
import org.example.honorsparkingbe.util.client.KakaoLocalSearchClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class KakaoLocalSearchClientTest {

  private static KakaoLocalSearchClient kakaoLocalSearchClient;
  static MockWebServer mockWebServer;

  private WebClient.Builder webClientBuilder;
  private WebClient webClient;

  @BeforeAll
  static void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    // mockWebServer 세팅
    mockWebServer.setDispatcher(new Dispatcher() {
      @NotNull
      @Override
      public MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
        String path = request.getPath();
        HttpUrl url = request.getRequestUrl();

        String query = url.queryParameter("query");
        String x = url.queryParameter("x");
        String y = url.queryParameter("y");
        String page = url.queryParameter("page");

        if (query.equals("wrongKyeowrd")) {
          return new MockResponse().setResponseCode(500).setBody("{\"error\":\"wrongKeyowrd\"}");
        }

        if (!path.contains("/search/keyword")) {
          return new MockResponse().setResponseCode(404);
        }

        try {
          if ("testKeyword1".equals(query) && x == null && y == null) {
            return jsonResponse(responseFor("testKeyword1"));
          } else if ("testKeyword2".equals(query) && "127.77".equals(x) && "36.55".equals(y)
              && page == null) {
            return jsonResponse(responseFor("testKeyword2"));
          } else if ("테스트 키워드3".equals(query) && "127.77".equals(x) && "36.55".equals(y)
              && "2".equals(page)) {
            return jsonResponse(responseFor("테스트 키워드3"));
          } else {
            return new MockResponse().setResponseCode(404);
          }
        } catch (Exception e) {
          System.out.println(e.getMessage());
          return new MockResponse().setResponseCode(500).setBody("Internal Server Error");
        }
      }
    });
    mockWebServer.start(0); // available port 자동 할당
    String mockBaseUrl = mockWebServer.url("/").toString();
    System.out.println("Kakao Local Search API URL: " + mockBaseUrl);

  }

  @AfterAll
  static void shutdownMockWebServer() throws IOException {
    mockWebServer.shutdown();
  }

  @BeforeEach
  void setUpEach() {
    String mockBaseUrl = mockWebServer.url("/").toString();
    String mockApiKey = "test-api-key";
    kakaoLocalSearchClient = new KakaoLocalSearchClient(mockApiKey, mockBaseUrl);
  }


  private static MockResponse jsonResponse(Object obj) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return new MockResponse()
        .setResponseCode(200)
        .addHeader("Content-Type", "application/json")
        .setBody(objectMapper.writeValueAsString(obj));
  }

  /**
   * placeName인 keyWord만 담아서 Mock Response제작
   *
   * @param placeName
   * @return
   * @throws IOException
   */
  private static KakaoLocalClientResponse responseFor(String placeName) throws IOException {

    KakaoLocalClientResponse res = KakaoLocalClientResponse
        .builder()
        .meta(Meta
            .builder()
            .sameName(SameName.builder()
                .region(List.of())
                .keyword(placeName)
                .selectedRegion("")
                .build())
            .pageableCount(1)
            .totalCount(1)
            .isEnd(true)
            .build()

        )
        .documents(List.of(
            Document.builder()
                .placeName(placeName + "강남점")
                .distance("123")
                .placeUrl("")
                .categoryName(placeName + "category")
                .addressName(placeName + "address")
                .roadAddressName(placeName + "roadAddress")
                .id("123123")
                .phone("")
                .categoryGroupCode("")
                .categoryGroupName("")
                .x("127.789")
                .y("37.567")
                .build()
        ))
        .build();

    return res;
  }

  @Test
  @DisplayName("keyword 올바른 Parm조건만 있을 때 Test")
  void kakaoLocalSearchClientTest_When_Right_Parm() throws IOException {
    // Given
    String testKeyword1 = "testKeyword1"; // 기본 키워드 검색 (쿼리만 있는 경우)
    String testKeyword2 = "testKeyword2"; // 키워드 + 좌표 (x, y)
    String testKeyword3 = "테스트 키워드3"; // 키워드 + 좌표 + 페이지 (x, y, page)

    SearchLocalDTO dto1 = SearchLocalDTO.builder()
        .keyword(testKeyword1)
        .build();
    SearchLocalDTO dto2 = SearchLocalDTO.builder()
        .keyword(testKeyword2)
        .latitudeY(36.55)
        .longitudeX(127.77)
        .build();
    SearchLocalDTO dto3 = SearchLocalDTO.builder()
        .keyword(testKeyword3)
        .latitudeY(36.55)
        .longitudeX(127.77)
        .page(1)
        .build();

    KakaoLocalClientResponse expectRes1 = responseFor(testKeyword1);
    KakaoLocalClientResponse expectRes2 = responseFor(testKeyword2);
    KakaoLocalClientResponse expectRes3 = responseFor(testKeyword3);

    KakaoLocalClientResponse res1 = kakaoLocalSearchClient.getLoocalsByKeyword(dto1);
    KakaoLocalClientResponse res2 = kakaoLocalSearchClient.getLoocalsByKeyword(dto2);
    KakaoLocalClientResponse res3 = kakaoLocalSearchClient.getLoocalsByKeyword(dto3);

    assertEquals(res1, expectRes1);
    assertEquals(res2, expectRes2);
    assertEquals(res3, expectRes3);
  }

  @Test
  @DisplayName("client가 요청에 실패했을 때 500에러 잘 던지는지 확인")
  void kakaoLocalSearchClientTest_When_Error_Occured() throws IOException {
    // Given
    String wrongKyeowrd = "wrongKyeowrd"; // 기본 키워드 검색 (쿼리만 있는 경우)

    SearchLocalDTO dto = SearchLocalDTO.builder()
        .keyword(wrongKyeowrd)
        .build();

    // When & Then
    assertThrows(RuntimeException.class, () -> {
      kakaoLocalSearchClient.getLoocalsByKeyword(dto); // 여기서 예외가 발생하길 기대
    });
  }

}
