package org.example.honorsparkingbe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.dto.request.ToggleFavoriteParkingZoneRequest;

@Getter
@Setter
@Builder
public class ToggleFavoriteParkingZoneDTO {

  ToggleFavoriteParkingZoneRequest toggleFavoriteParkingZoneRequest;
  Long userId;
}
