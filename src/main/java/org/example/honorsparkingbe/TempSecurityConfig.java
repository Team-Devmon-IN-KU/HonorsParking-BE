//package org.example.honorsparkingbe;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@Profile("test")
//public class TempSecurityConfig {
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//    //csrf disable
//    http
//        .csrf(csrf -> csrf.disable()) // CSRF 비활성화
//        .authorizeHttpRequests(auth -> auth
//            .anyRequest().permitAll()); // 모든 요청 허용
//
//    return http.build();
//  }
//}
