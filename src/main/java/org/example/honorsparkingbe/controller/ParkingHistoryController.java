package org.example.honorsparkingbe.controller;

import static org.example.honorsparkingbe.security.util.SecurityUtil.getCurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.DeleteParkingHistoryDTO;
import org.example.honorsparkingbe.dto.request.ParkingHistoryDeleteRequest;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parking/history")
@RequiredArgsConstructor
public class ParkingHistoryController {

  private final ParkingHistoryService parkingHistoryService;

  @DeleteMapping
  public ResponseEntity<?> deleteParkingHistory(
      @Valid @RequestBody ParkingHistoryDeleteRequest request
  ) {

    Long userId = getCurrentUserId();
    
    return ResponseEntity.ok(
        parkingHistoryService.softDeleteParkingHistories(
            DeleteParkingHistoryDTO.builder()
                .paringHistoryDeleteRequest(request)
                .userId(userId)
                .build()
        ));
  }
}
