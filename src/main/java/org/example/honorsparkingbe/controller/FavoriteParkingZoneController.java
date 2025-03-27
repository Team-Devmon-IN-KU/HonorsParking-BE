package org.example.honorsparkingbe.controller;

import static org.example.honorsparkingbe.security.util.SecurityUtil.getCurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.AddFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.DeleteFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.request.AddFavoriteParkingZoneRequest;
import org.example.honorsparkingbe.dto.request.DeleteFavoriteParkingZoneRequest;
import org.example.honorsparkingbe.dto.response.AddFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.dto.response.DeleteFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.FavoriteParkingZoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  public ResponseEntity<AddFavoriteParkingZoneResponse> createFavoriteParkingZone(
      @Valid @RequestBody AddFavoriteParkingZoneRequest request) {

    Long userId = getCurrentUserId();
    return ResponseEntity.ok(
        favoriteParkingZoneService.addFavoriteParkingZone(
            AddFavoriteParkingZoneDTO.builder()
                .userId(userId)
                .addFavoriteParkingZoneRequest(request)
                .build()
        )
    );
  }

  @DeleteMapping
  public ResponseEntity<DeleteFavoriteParkingZoneResponse> deleteFavoriteParkingZone(
      @Valid @RequestBody DeleteFavoriteParkingZoneRequest request) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    return ResponseEntity.ok(
        favoriteParkingZoneService.deleteFavoriteParkingZone(
            DeleteFavoriteParkingZoneDTO.builder()
                .userId(customUserDetails.getId())
                .deleteFavoriteParkingZoneRequest(request)
                .build()
        )
    );
  }
}
