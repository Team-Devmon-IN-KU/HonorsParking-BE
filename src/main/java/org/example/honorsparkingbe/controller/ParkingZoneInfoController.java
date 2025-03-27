package org.example.honorsparkingbe.controller;

import static org.example.honorsparkingbe.security.util.SecurityUtil.getCurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ParkingZoneListDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.service.ParkingZoneInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parkingzone")
@RequiredArgsConstructor
public class ParkingZoneInfoController {

  private final ParkingZoneInfoService parkingZoneInfoService;

  /**
   * 파킹존 리스트를 불러오는 컨트롤러 /api/v1/parkingzone/list
   *
   * @param request
   * @return
   */
  @GetMapping("/list")
  public ResponseEntity<ParkingZoneListResponse> getParkingZoneList(
      @Valid @ModelAttribute ParkingZoneListRequest request
  ) {

    Long userId = getCurrentUserId();

    ParkingZoneListResponse response = parkingZoneInfoService
        .getParkingZones(
            ParkingZoneListDTO.builder()
                .parkingZoneListRequest(request)
                .userId(userId)
                .build()
        );
    return ResponseEntity.ok(response);
  }
}
