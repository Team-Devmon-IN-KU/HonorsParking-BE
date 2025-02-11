package org.example.honorsparkingbe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.honorsparkingbe.dto.request.DeleteFavoriteParkingZoneRequest;

@Getter
@Setter
@Builder
public class DeleteFavoriteParkingZoneDTO {

  DeleteFavoriteParkingZoneRequest deleteFavoriteParkingZoneRequest;
  Long userId;
}
