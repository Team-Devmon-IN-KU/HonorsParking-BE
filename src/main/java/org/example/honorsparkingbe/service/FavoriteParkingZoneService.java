package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.ToggleFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.response.ToggleFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.repository.FavoriteParkingZoneRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteParkingZoneService {

  private final FavoriteParkingZoneRepository favoriteParkingZoneRepository;

  public ToggleFavoriteParkingZoneResponse toggleFavoriteParkingZone(
      ToggleFavoriteParkingZoneDTO toggleFavoriteParkingZoneDTO) {

    return ToggleFavoriteParkingZoneResponse.builder().build();

  }
}
