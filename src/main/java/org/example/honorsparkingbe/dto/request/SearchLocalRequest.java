package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchLocalRequest {

  @NotNull(message = "keyword 쿼리 파라매터는 필수입니다.")
  private String keyword;
  private Double longitudeX;
  private Double latitudeY;
  @Min(value = 0, message = "page는 0 이상이어야 합니다.")
  private Integer page;

  @AssertTrue(message = "latitude와 longitude는 둘 다 존재하거나 둘 다 없어야 합니다.")
  public boolean isCoordinateValid() {
    return (longitudeX == null && latitudeY == null) ||
        (longitudeX != null && latitudeY != null);
  }
}
