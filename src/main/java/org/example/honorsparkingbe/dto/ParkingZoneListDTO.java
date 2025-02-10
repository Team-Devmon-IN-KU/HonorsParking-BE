package org.example.honorsparkingbe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;

@Getter
@Setter
@Builder
public class ParkingZoneListDTO {

  ParkingZoneListRequest parkingZoneListRequest;
  Long userId;
}
