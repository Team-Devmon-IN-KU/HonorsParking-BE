package org.example.honorsparkingbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.dto.request.ParkingHistoryDeleteRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteParkingHistoryDTO {

  ParkingHistoryDeleteRequest paringHistoryDeleteRequest;
  Long userId;
}
