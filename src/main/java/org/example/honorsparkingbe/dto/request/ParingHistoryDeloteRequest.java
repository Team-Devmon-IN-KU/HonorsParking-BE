package org.example.honorsparkingbe.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParingHistoryDeloteRequest {

    @NotNull(message = "historyIDList는 null일 수 없습니다.")
    @NotEmpty(message = "historyIDList는 최소 1개의 ID를 포함해야 합니다.")
    @Size(min = 1, message = "historyIDList는 최소 1개의 요소를 포함해야 합니다.")
    List<Long> historyIDList;

}
