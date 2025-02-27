package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.example.honorsparkingbe.controller.PaymentInfoController;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.PaymentInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest
@Import({Slf4jRestControllerAdvice.class})  // 예외 처리 적용
@ContextConfiguration(classes = SecurityConfig.class)  // SecurityConfig 추가
public class PaymentInfoControllerTest {

  @Mock
  private MemberEntity memberEntity;
  @Mock
  private PaymentInfoService paymentInfoService;
  @InjectMocks
  private PaymentInfoController paymentInfoController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    memberEntity = MemberEntity.builder()
        .password("password")
        .role(MemberRole.ROLE_USER)
        .userName("testUserName")
        .id(100L)
        .build();

    // CustomUserDetails 생성
    CustomUserDetails customUserDetails = new CustomUserDetails(memberEntity);

    // SecurityContext에 설정
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(new UsernamePasswordAuthenticationToken(customUserDetails, null,
        customUserDetails.getAuthorities()));
    SecurityContextHolder.setContext(context);

    // mockMvc설정
    mockMvc = MockMvcBuilders
        .standaloneSetup(paymentInfoController)
        .setCustomArgumentResolvers()
        .build();
  }

  @Test
  void getPaymentInfo() throws Exception {

    // When
    mockMvc.perform(get("/api/v1/pakring/paymentInfo")).andExpect(status().isOk());

    verify(paymentInfoService, times(1)).getPaymentInfo(any());
  }
}
