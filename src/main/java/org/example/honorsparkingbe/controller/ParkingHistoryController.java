package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.request.ParingHistoryDeloteRequest;
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
      @Valid @RequestBody ParingHistoryDeloteRequest request) {

    throw new RuntimeException("sdsdsd");
//    return ResponseEntity.ok(
//        parkingHistoryService.softDeleteParkingHistories(request.getHistoryIDList()));
  }
}
