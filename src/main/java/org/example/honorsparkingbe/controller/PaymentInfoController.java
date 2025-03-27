package org.example.honorsparkingbe.controller;

import static org.example.honorsparkingbe.security.util.SecurityUtil.getCurrentUserId;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.PaymentInfoDTO;
import org.example.honorsparkingbe.dto.response.GetPaymentInfoResponse;
import org.example.honorsparkingbe.service.PaymentInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pakring/paymentInfo")
@RequiredArgsConstructor
public class PaymentInfoController {


  private final PaymentInfoService paymentInfoService;

  @GetMapping()
  public ResponseEntity<GetPaymentInfoResponse> getPaymentInfo(HttpServletResponse response) {
    // Authentication을 통해 사용자 정보 가져오기
    Long userId = getCurrentUserId();

    // 서비스에 사용자 ID와 응답 객체를 전달
    GetPaymentInfoResponse paymentInfoResponse = paymentInfoService.getPaymentInfo(
        PaymentInfoDTO.builder()
            .userId(userId)
            .build(),
        response
    );

    return ResponseEntity.ok(paymentInfoResponse);
  }

}
