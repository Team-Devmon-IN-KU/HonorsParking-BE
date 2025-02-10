package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ParkingHistoryDeleteRequest {

  @NotNull(message = "historyIDList는 null일 수 없습니다.")
  @NotEmpty(message = "historyIDList는 최소 1개의 ID를 포함해야 합니다.")
  @Size(min = 1, message = "historyIDList는 최소 1개의 요소를 포함해야 합니다.")
  List<Long> historyIDList;

}
