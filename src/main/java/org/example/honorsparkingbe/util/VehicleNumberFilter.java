package org.example.honorsparkingbe.util;

import org.springframework.stereotype.Component;

@Component
public class VehicleNumberFilter {

  // 차량 번호 필터링 메서드
  public String formatLicensePlate(String input) {
    // 처음에 숫자가 아니라면 숫자가 나올 때까지 앞의 문자를 제거
    if (!Character.isDigit(input.charAt(0))) {
      input = input.replaceAll("^[^0-9]+", "");  // 처음에 숫자가 아닌 문자들을 제거
    }

    // 중간에 들어가는 문자가 숫자가 아니면 구분자 없이 그대로 출력
    return input.replaceAll("([0-9]+)([^0-9]+)([0-9]+)", "$1$2$3");
  }
}
