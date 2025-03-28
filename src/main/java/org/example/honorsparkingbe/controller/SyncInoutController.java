package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.request.SyncInoutRequest;
import org.example.honorsparkingbe.dto.response.SyncInoutResponse;
import org.example.honorsparkingbe.service.SyncInoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/sync/inout")
@RequiredArgsConstructor
public class SyncInoutController {

  private final SyncInoutService syncInoutService;

  @PostMapping
  public ResponseEntity<SyncInoutResponse> updateSyncInout(
      @Valid @RequestBody SyncInoutRequest request) {

    return ResponseEntity.ok(syncInoutService.syncParkingHistory(request));
  }
}
