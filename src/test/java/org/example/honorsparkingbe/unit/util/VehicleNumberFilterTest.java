package org.example.honorsparkingbe.unit.util;

import static org.junit.Assert.assertEquals;

import org.example.honorsparkingbe.util.VehicleNumberFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VehicleNumberFilterTest {

  @InjectMocks
  private VehicleNumberFilter vehicleNumberFilter;

  @Test
  @DisplayName("차량 번호가 '경기92배1326'일 때, '92배1326'로 필터링이 되어야 한다.")
  public void testFormatLicensePlate_withNonNumericPrefix() {
    // 예시: "경기92배1326"
    String input = "경기92배1326";
    String expected = "92배1326";

    String result = vehicleNumberFilter.formatLicensePlate(input);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("차량 번호가 '92배1326'일 때, 그대로 '92배1326'이 반환되어야 한다.")
  public void testFormatLicensePlate_withNumericPrefix() {
    // 예시: "92배1326"
    String input = "92배1326";
    String expected = "92배1326";  // 변경 없음

    String result = vehicleNumberFilter.formatLicensePlate(input);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("차량 번호가 '서울1배1326'일 때, '1배1326'로 필터링이 되어야 한다.")
  public void testFormatLicensePlate_withOnlyText() {
    // 예시: "서울배1326"
    String input = "서울1배1326";
    String expected = "1배1326";

    String result = vehicleNumberFilter.formatLicensePlate(input);

    assertEquals(expected, result);
  }

  @Test
  @DisplayName("차량 번호가 '서울92배1326'일 때, '92배1326'로 필터링이 되어야 한다.")
  public void testFormatLicensePlate_withMixedText() {
    // 예시: "서울92배1326"
    String input = "서울92배1326";
    String expected = "92배1326";

    String result = vehicleNumberFilter.formatLicensePlate(input);

    assertEquals(expected, result);
  }
}
