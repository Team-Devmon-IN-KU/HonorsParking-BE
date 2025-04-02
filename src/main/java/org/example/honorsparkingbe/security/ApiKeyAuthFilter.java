package org.example.honorsparkingbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

  @Value("${api.key}")
  private String VALID_API_KEY;
  private static final String API_KEY_HEADER = "X-API-KEY";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    // 특정 URL 패턴에 대해서만 API Key 검증 수행
    if (request.getRequestURI().startsWith("/api/v1/sync/inout")) {
      String apiKey = request.getHeader(API_KEY_HEADER);

      if (apiKey == null || !apiKey.equals(VALID_API_KEY)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
