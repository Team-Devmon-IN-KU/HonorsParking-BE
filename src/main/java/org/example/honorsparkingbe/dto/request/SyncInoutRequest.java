package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncInoutRequest {

  @NotNull(message = "inoutList는 필수입니다.")
  private List<Inout> inoutList;

  @Builder
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Inout {

    @NotNull(message = "차량 번호는 필수입니다.")
    private String vehicleNumber;

    @NotNull(message = "입차 ID는 필수입니다.")
    private Long entryId;

    @NotNull(message = "입차 시간은 필수입니다.")
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;
    private Integer fee;
    private LocalDateTime paidAt;

    @NotNull(message = "주차장 ID는 필수입니다.")
    private Long parkinglotId;
  }
}