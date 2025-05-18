package org.example.honorsparkingbe.controller;

import static org.example.honorsparkingbe.security.util.SecurityUtil.getCurrentUserId;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.dto.request.SearchLocalRequest;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse;
import org.example.honorsparkingbe.service.SearchLocalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/search/local")
@RequiredArgsConstructor
public class SearchLocalController {

  private final SearchLocalService searchLocalService;

  @GetMapping
  ResponseEntity<SearchLocalResponse> searchLocal(
      @ModelAttribute @Valid SearchLocalRequest request) {

    return ResponseEntity.ok(searchLocalService.getLocalInfoByKeyword(SearchLocalDTO.builder()
        .keyword(request.getKeyword())
        .memberId(getCurrentUserId())
        .page(request.getPage())
        .longitudeX(request.getLongitudeX())
        .latitudeY(request.getLatitudeY())
        .build()));
  }
}
