package org.example.honorsparkingbe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KakaoLocalClientResponse {

  @JsonProperty("meta")
  private Meta meta;

  @JsonProperty("documents")
  private List<Document> documents;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Meta {

    @JsonProperty("same_name")
    private SameName sameName;

    @JsonProperty("pageable_count")
    private int pageableCount;

    @JsonProperty("total_count")
    private int totalCount;

    @JsonProperty("is_end")
    private boolean isEnd;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SameName {

    private List<String> region;

    private String keyword;

    @JsonProperty("selected_region")
    private String selectedRegion;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Document {

    @JsonProperty("place_name")
    private String placeName;

    private String distance;

    @JsonProperty("place_url")
    private String placeUrl;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("road_address_name")
    private String roadAddressName;

    private String id;
    private String phone;

    @JsonProperty("category_group_code")
    private String categoryGroupCode;

    @JsonProperty("category_group_name")
    private String categoryGroupName;

    private String x;
    private String y;
  }
}
