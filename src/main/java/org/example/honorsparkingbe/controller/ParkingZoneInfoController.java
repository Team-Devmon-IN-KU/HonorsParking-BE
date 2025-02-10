package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ParkingZoneListDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.dto.response.ParkingZoneListResponse;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.ParkingZoneInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    ParkingZoneListResponse response = parkingZoneInfoService
        .getParkingZones(
            ParkingZoneListDTO.builder()
                .parkingZoneListRequest(request)
                .userId(customUserDetails.getId())
                .build()
        );
    return ResponseEntity.ok(response);
  }
}
