package org.example.honorsparkingbe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.dto.request.AddFavoriteParkingZoneRequest;

@Getter
@Setter
@Builder
public class AddFavoriteParkingZoneDTO {

  AddFavoriteParkingZoneRequest addFavoriteParkingZoneRequest;
  Long userId;
}
