package org.example.honorsparkingbe.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.example.honorsparkingbe.config.ApiKeyService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

  private static final String API_KEY_HEADER = "X-API-KEY";
  private final ApiKeyService apiKeyService;

  public ApiKeyAuthFilter(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    if (request.getRequestURI().startsWith("/api/v1/sync/inout")) {
      String apiKey = request.getHeader(API_KEY_HEADER);
      String validApiKey = apiKeyService.getApiKey();

      if (apiKey == null || !apiKey.equals(validApiKey)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}