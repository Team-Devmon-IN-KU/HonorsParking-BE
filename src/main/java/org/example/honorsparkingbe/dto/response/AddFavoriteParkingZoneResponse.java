package org.example.honorsparkingbe.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddFavoriteParkingZoneResponse {

  private boolean isSuccess;
  private Long parkingZoneId;
  private boolean isBookmark;
}
