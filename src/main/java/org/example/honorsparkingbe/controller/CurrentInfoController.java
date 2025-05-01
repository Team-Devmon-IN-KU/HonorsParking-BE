package org.example.honorsparkingbe.controller;

import java.util.Map;
import org.example.honorsparkingbe.dto.CustomOAuth2User;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.CurrentInfoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 주차장 이용상태 불러오기 - 현재 사용자의 주차정보, 주차시간, 요금 등
 */

@RestController
@RequestMapping("/api/v1/parking")
public class CurrentInfoController {

  private final CurrentInfoService currentInfoService;

  public CurrentInfoController(CurrentInfoService currentInfoService) {
    this.currentInfoService = currentInfoService;
  }

  /**
   * GET /api/v1/parking/me
   */
  @GetMapping("/me")
  public Map<String, Object> getParkingInfo() {
    // 세션으로부터 로그인된 사용자 ID를 받아오기
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();

    Long sessionMemberId = null;

    // principal이 CustomOAuth2User 타입인 경우 처리 (시간나면 두 클래스 하나의 인터페이스로 묶어보자)
    if (principal instanceof CustomOAuth2User) {
      sessionMemberId = ((CustomOAuth2User) principal).getId();
    } else if (principal instanceof CustomUserDetails) {
      sessionMemberId = ((CustomUserDetails) principal).getId();
    } else {
      throw new IllegalStateException(
          "Unexpected principal type: " + principal.getClass().getName());
    }

//        System.out.println("SessionMemberID: " + sessionMemberId);

    return currentInfoService.getCurrentParkingInfo(sessionMemberId);


  }


}
