package org.example.honorsparkingbe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ToggleFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.request.ToggleFavoriteParkingZoneRequest;
import org.example.honorsparkingbe.dto.response.ToggleFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.FavoriteParkingZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/parking/parkingzone/bookmark")
@RequiredArgsConstructor
public class FavoriteParkingZoneController {

  private final FavoriteParkingZoneService favoriteParkingZoneService;

  @PostMapping
  public ResponseEntity<ToggleFavoriteParkingZoneResponse> createFavoriteParkingZone(
      @Valid @RequestBody ToggleFavoriteParkingZoneRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    return ResponseEntity.ok(
        favoriteParkingZoneService.addFavoriteParkingZone(
            ToggleFavoriteParkingZoneDTO.builder()
                .userId(customUserDetails.getId())
                .toggleFavoriteParkingZoneRequest(request)
                .build()
        )
    );
  }

//  @DeleteMapping
//  public ResponseEntity<ToggleFavoriteParkingZoneResponse> deleteFavoriteParkingZone(
//      @Valid @RequestBody ToggleFavoriteParkingZoneRequest request) {
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
//
//    return ResponseEntity.ok(
//        favoriteParkingZoneService.toggleFavoriteParkingZone(
//            ToggleFavoriteParkingZoneDTO.builder()
//                .userId(customUserDetails.getId())
//                .toggleFavoriteParkingZoneRequest(request)
//                .build()
//        )
//    );
//  }
}
