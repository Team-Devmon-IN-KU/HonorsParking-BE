package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeleteFavoriteParkingZoneRequest {

  @NotNull(message = "parkingZoneId 는 필수입니다.")
  private Long parkingZoneId;
}
