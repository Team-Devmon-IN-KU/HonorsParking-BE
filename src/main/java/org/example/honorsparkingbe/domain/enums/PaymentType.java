package org.example.honorsparkingbe.domain.enums;

public enum PaymentType {
    ON_SITE,        // 현장 결제
    KIOSK,          // 키오스크 결제
    CARD_APP,       // 앱 등록된 카드로 결제
    GENERAL_APP,    // 앱 일반 결제
    NONE,           // 결제 안됨
    FAULT,          // 미납
    OTHER           // 기타
}