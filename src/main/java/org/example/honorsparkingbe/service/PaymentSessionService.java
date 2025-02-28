package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.domain.session.PaymentSessionManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentSessionService {

  private PaymentSessionManager paymentSessionManager;

  public void createPaymentSession(Long userId, Long amount) {
    paymentSessionManager.createSession(userId, amount);
  }

  public boolean validatePaymentSession(Long userId) {
    return paymentSessionManager.isValidSession(userId);
  }

  public void removePaymentSession(Long userId) {
    paymentSessionManager.deleteSession(userId);
  }
}
