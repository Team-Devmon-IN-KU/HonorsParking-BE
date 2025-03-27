package org.example.honorsparkingbe.security.util;

import org.example.honorsparkingbe.dto.CustomOAuth2User;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

  public static Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication.getPrincipal() == "anonymousUser") {
      return null; // 비로그인 상태
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof CustomOAuth2User) {
      return ((CustomOAuth2User) principal).getId();
    } else if (principal instanceof CustomUserDetails) {
      return ((CustomUserDetails) principal).getId();
    } else {
      throw new IllegalStateException(
          "Unexpected principal type: " + principal.getClass().getName());
    }
  }
}
