package org.example.honorsparkingbe.controller;

/**
 * 로그아웃 API 테스트 - 세션, 인증 정보가 제대로 삭제되는지 - 인가 없는 상태에서 요청 들어왔을 떄 처리 -> 생각해보니 로그인 페이지로 이동시키게 설정되어 있음
 */

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class LogoutControllerTest {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    // LogoutController 초기화
    LogoutController logoutController = new LogoutController();
    mockMvc = MockMvcBuilders.standaloneSetup(logoutController).build();
  }

//    @Test
//    void logout_ShouldReturnOk_WhenAuthenticated() throws Exception {
//        // Given: Mock SecurityContext 및 Authentication 설정
//        Authentication mockAuthentication = Mockito.mock(Authentication.class);
//        SecurityContext mockSecurityContext = Mockito.mock(SecurityContext.class);
//
//        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
//        SecurityContextHolder.setContext(mockSecurityContext);
//
//        // When & Then
//        mockMvc.perform(get("/logout"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("logout succcessful"));
//
//        // Verify
//        verify(mockSecurityContext, times(1)).getAuthentication();
//    }
}

