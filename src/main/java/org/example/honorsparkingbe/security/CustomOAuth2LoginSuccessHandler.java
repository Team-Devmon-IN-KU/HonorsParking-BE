package org.example.honorsparkingbe.security;
/**
 * OAuth2 성공 핸들러 (리다이렉트만 수행)
 */

import org.example.honorsparkingbe.dto.CustomOAuth2User;
import org.example.honorsparkingbe.dto.OAuth2Response;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 로그인 성공 후 프론트엔드로 리디렉트
        // response.sendRedirect("http://localhost:3000/");
        // response.sendRedirect("https://honorsparking-web.vercel.app/");
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        String role= user.getAuthorities().iterator().next().getAuthority();

        if("ROLE_USER".equals(role)){
            response.sendRedirect("https://honorsparking-web.vercel.app/");
        }else{
            response.sendRedirect("https://honorsparking-web.vercel.app/hello");
        }
    }
}