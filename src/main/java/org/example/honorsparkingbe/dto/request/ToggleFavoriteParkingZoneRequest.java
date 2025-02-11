package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToggleFavoriteParkingZoneRequest {

  @NotNull(message = "parkingZoneId 는 필수입니다.")
  private Long parkingZoneId;
  @NotNull(message = "isBookmark는 필수입니다.")
  private boolean isBookmark;

}
