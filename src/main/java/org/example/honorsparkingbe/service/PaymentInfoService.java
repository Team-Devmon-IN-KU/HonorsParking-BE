package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.dto.PaymentInfoDTO;
import org.example.honorsparkingbe.dto.response.GetPaymentInfoResponse;
import org.example.honorsparkingbe.dto.response.GetPaymentInfoResponse.Card;
import org.springframework.stereotype.Service;

@Service
public class PaymentInfoService {

  public GetPaymentInfoResponse getPaymentInfo(PaymentInfoDTO paymentInfoDTO) {

    Long userId = paymentInfoDTO.getUserId();

    // 1. Member ID를 기준으로 차량 최종요금 센터서버에서 가져오기

    // 2. 결제가 유효안 5분 동안의 세션만들기

    // 3. Member ID로 등록된 카드 정보 가져오기

    return GetPaymentInfoResponse.builder()
        .card(Card.builder()
            .cardName("sdf")
            .hashedCardNumber("123")
            .build())
        .cost(12345L)
        .build();

  }

}
