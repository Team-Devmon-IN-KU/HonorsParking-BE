package org.example.honorsparkingbe.dto.response;

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

public class ParkingHistoryDeleteResponse {

  Boolean isSuccess;
  List<Long> deletedIds; //성공 삭제 ID리스트
  List<Long> failedIds; //성공 실패 ID리스트
}
