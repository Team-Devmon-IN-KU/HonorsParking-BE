package org.example.honorsparkingbe.util;

import org.springframework.stereotype.Component;

@Component
public class VehicleNumberFilter {

  /**
   * 차량 번호를 처리하여 특정 형식으로 변환하는 메서드입니다. 이 메서드는 입력된 차량 번호에서 특정 패턴을 제거하거나 수정하여 변환된 번호를 반환합니다.
   * <p>
   * 예시: - 입력: "경기92배1326", 출력: "92배1326" - 입력: "1234AB5678", 출력: "1234AB5678" (변경 없음)
   *
   * @param input 차량 번호 문자열
   * @return 변환된 차량 번호 문자열
   */
  public String formatLicensePlate(String input) {
    // 처음에 숫자가 아니라면 숫자가 나올 때까지 앞의 문자를 제거
    if (!Character.isDigit(input.charAt(0))) {
      input = input.replaceAll("^[^0-9]+", "");  // 처음에 숫자가 아닌 문자들을 제거
    }

    // 중간에 들어가는 문자가 숫자가 아니면 구분자 없이 그대로 출력
    return input.replaceAll("([0-9]+)([^0-9]+)([0-9]+)", "$1$2$3");
  }
}
