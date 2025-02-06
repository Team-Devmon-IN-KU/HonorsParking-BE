package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.response.ParingHistoryDeleteResponse;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ParkingHistoryService {

    private final ParkingHistoryRepository parkingHistoryRepository;

    public ParingHistoryDeleteResponse softDeleteParkingHistories(List<Long> targetIds){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        System.out.println(userId);
        // 1. 해당 ID에 대한 권한 확인
        parkingHistoryRepository.findByIdsAndMember(targetIds, userId);
        // 본인의 이력이 맞는지 검증

        // 2. 본인 ID가 아닌 것은 필터링

        return ParingHistoryDeleteResponse.builder()
                .isSuccess(targetIds.size() == 3)
                .deletedIds(targetIds)
                .failedIds(targetIds)
                .build();

    }
}
