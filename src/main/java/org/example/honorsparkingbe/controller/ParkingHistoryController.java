package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.DeleteParkingHistoryDTO;
import org.example.honorsparkingbe.dto.request.ParkingHistoryDeleteRequest;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    return ResponseEntity.ok(
        parkingHistoryService.softDeleteParkingHistories(
            DeleteParkingHistoryDTO.builder()
                .paringHistoryDeleteRequest(request)
                .userId(customUserDetails.getId())
                .build()
        ));
  }
}
