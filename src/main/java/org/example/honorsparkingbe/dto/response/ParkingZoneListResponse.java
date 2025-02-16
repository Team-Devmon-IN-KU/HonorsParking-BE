package org.example.honorsparkingbe.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.dto.ParkingZoneDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingZoneListResponse {

  private List<ParkingZoneDTO> parkingZones;
  private PaginationInfo pagination;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class PaginationInfo {

    private int currentPage;
    private int totalPages;
    private int pagePerItem;
    private int totalItems;
  }
}
