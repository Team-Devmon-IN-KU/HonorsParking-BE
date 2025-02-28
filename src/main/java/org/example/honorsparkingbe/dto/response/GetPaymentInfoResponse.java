package org.example.honorsparkingbe.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetPaymentInfoResponse {

  private Card card;
  private Long cost;
  private String inCarImage;

  @Builder
  @Getter
  public static class Card {

    private String cardName;
    private String hashedCardNumber;
  }
}
