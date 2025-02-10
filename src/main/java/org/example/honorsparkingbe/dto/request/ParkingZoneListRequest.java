package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ParkingZoneListRequest {

  @NotNull(message = "위도(latitude)는 필수입니다.")
  @Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
  @Max(value = 90, message = "위도는 90 이하이어야 합니다.")
  private Double latitude;

  @NotNull(message = "경도(longitude)는 필수입니다.")
  @Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
  @Max(value = 180, message = "경도는 180 이하이어야 합니다.")
  private Double longitude;

  private Long page = 0L;  // 기본값을 설정
}
