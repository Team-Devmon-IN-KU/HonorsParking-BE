package org.example.honorsparkingbe.controller;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.PaymentInfoDTO;
import org.example.honorsparkingbe.dto.response.GetPaymentInfoResponse;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.PaymentInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pakring/paymentInfo")
@RequiredArgsConstructor
public class PaymentInfoController {


  private final PaymentInfoService paymentInfoService;

  @GetMapping
  public ResponseEntity<GetPaymentInfoResponse> getPaymentInfo() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

    return ResponseEntity.ok(
        paymentInfoService.getPaymentInfo(
            PaymentInfoDTO.builder()
                .userId(customUserDetails.getId())
                .build())
    );
  }

}
