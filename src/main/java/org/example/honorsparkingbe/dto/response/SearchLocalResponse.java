package org.example.honorsparkingbe.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchLocalResponse {

  private Meta meta;
  private List<KakaoLocalDocument> documents;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Meta {

    private String keyword;
    private Boolean isEnd;
    private PaginationResponse pagination;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class KakaoLocalDocument {

    private String placeName;
    private String distance;
    private String categoryName;
    private String addressName;
    private String roadAddressName;
    private double y;
    private double x;
  }
}
