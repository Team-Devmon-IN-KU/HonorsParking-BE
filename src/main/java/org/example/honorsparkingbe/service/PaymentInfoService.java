package org.example.honorsparkingbe.service;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.entity.CardEntity;
import org.example.honorsparkingbe.domain.session.PaymentSessionManager;
import org.example.honorsparkingbe.dto.PaymentInfoDTO;
import org.example.honorsparkingbe.dto.response.GetPaymentInfoResponse;
import org.example.honorsparkingbe.dto.response.GetPaymentInfoResponse.Card;
import org.example.honorsparkingbe.repository.internal.CardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/**
 * 센터 서버에서 데이터를 받고 그 이후 테스트코드 작성 예정
 */
public class PaymentInfoService {

  private final PaymentSessionManager paymentSessionManager;
  private final CardRepository cardRepository;

  public GetPaymentInfoResponse getPaymentInfo(PaymentInfoDTO paymentInfoDTO,
      HttpServletResponse response) {

    Long userId = paymentInfoDTO.getUserId();

    // 1. Member ID를 기준으로 차량 최종요금 및 입차 사진 센터서버에서 가져오기

    // 2. 결제가 유효안 5분 동안의 세션만들기
    paymentSessionManager.createSession(userId, 5L, response);
    // 3. Member ID로 등록된 카드 정보 가져오기
    List<CardEntity> userCardList = cardRepository.findCardEntityByMemberEntity_Id(userId);

    // 3-1. 카드가 존재하면 정보를 반환하고, 없으면 null로 설정
    Card card = null;
    if (!userCardList.isEmpty()) {
      CardEntity userCard = userCardList.get(0); // 첫 번째 카드 정보
      card = Card.builder()
          .cardName(userCard.getCardNickname())
          .hashedCardNumber(userCard.getHashedCardNumber())
          .build();
    }

    return GetPaymentInfoResponse.builder()
        .card(card) // 카드 정보가 없으면 null 반환
        .cost(12345L) // 비용 정보는 그대로 유지
        .inCarImage(
            "https://res.cloudinary.com/dhabktrg9/image/upload/v1740731978/mg8mjclgfydyqjtmiwt1.png")
        .build();

  }
}
