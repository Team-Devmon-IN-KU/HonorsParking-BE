package org.example.honorsparkingbe.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SyncInoutResponse {

  List<ParkingEntry> ValidNonExitEntries;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ParkingEntry {

    private Long id;
  }
}
