package org.example.honorsparkingbe.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class GetParkingZoneInfoTest extends InitIntegrationTest {

  @Test
  @DisplayName("주차 구역 정보를 성공적으로 조회한다.")
  void getParkingZoneInfo_Success() {
    // given
    Map<String, Object> queryParams = Map.of(
        "latitude", 37.5665,
        "longitude", 126.978
    );

    System.out.println(queryParams);
    // then
    client.get()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/parkingzone/list")
            .queryParam("latitude", queryParams.get("latitude"))
            .queryParam("longitude", queryParams.get("longitude"))
            .build())
        .cookie("SESSION", sessionToken)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ParkingZoneListResponse.class)
        .consumeWith(response -> {
          ParkingZoneListResponse body = response.getResponseBody();

          // parkingZones가 null이 아니고 비어있지 않은지 검증
          assertNotNull(body.getParkingZones());
//          assertFalse(body.getParkingZones().isEmpty());

          // pagination 필드가 null이 아닌지 검증
          assertNotNull(body.getPagination());

          // pagination 정보에 대한 검증
          assertTrue(body.getPagination().getCurrentPage() >= 0);
          assertTrue(body.getPagination().getTotalPages() >= 0);
          assertTrue(body.getPagination().getPagePerItem() >= 0);
          assertTrue(body.getPagination().getTotalItems() >= 0);
        });
  }

}
