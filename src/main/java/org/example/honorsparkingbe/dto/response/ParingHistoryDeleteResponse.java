package org.example.honorsparkingbe.dto.response;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder

public class ParingHistoryDeleteResponse {

    Boolean isSuccess;
    List<Long> deletedIds; //성공 삭제 ID리스트
    List<Long> failedIds; //성공 실패 ID리스트
}
