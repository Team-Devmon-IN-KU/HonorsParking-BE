package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.honorsparkingbe.controller.AlarmController;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.context.annotation.Import;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
public class AlarmControllerAuthTest {

    @InjectMocks
    private AlarmController alarmController;

    @Mock
    private AlarmService alarmService;

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(alarmController)
                .build();

        // SecurityContext에 사용자 인증 정보 설정
        MemberEntity mockMember = new MemberEntity();
        mockMember.setId(1L);
        mockMember.setAuthId("testuser");
        mockMember.setRole(MemberRole.ROLE_USER); // 명시적 역할 설정

        CustomUserDetails customUserDetails = new CustomUserDetails(mockMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    /**
     * 1. 알람 목록 조회 테스트 (GET /api/v1/alarmAll)
     */
    @Test
    @DisplayName("알람 목록 조회 성공")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAlarms_Success() throws Exception {

        // Given
        Map<String, Object> mockResponse = Map.of(
                "pagination", Map.of("currentPage", 1),  // 0페이지가 입력되면 +1을 적용하는 로직
                "alarms", List.of(Map.of("id", 1L, "content", "Test Alarm"))
        );

        when(alarmService.getAlarms(any(Long.class), any(), any(Integer.class), any(Integer.class)))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/alarmAll")
                        .param("category", "info")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(2))  // 기존 1 → 2로 수정
                .andExpect(jsonPath("$.alarms[0].content").value("Test Alarm"));
    }

    /**
     * 읽지 않은 알람만 조회
     */
    @Test
    @DisplayName("읽지 않은 알람 목록 조회 성공")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetUnreadAlarms_Success() throws Exception {
        // Given
        Map<String, Object> mockResponse = Map.of(
                "pagination", Map.of("currentPage", 0),
                "alarms", List.of(
                        Map.of("id", 1L, "content", "미확인 알람", "isRead", "UNREAD")
                )
        );

        when(alarmService.getUnreadAlarms(any(Long.class), any(), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/alarmUnread")
                        .param("category", "INOUT")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(1)) // 서비스에서는 0 기반, 컨트롤러에서 +1 처리됨
                .andExpect(jsonPath("$.alarms[0].isRead").value("UNREAD"));
    }




    /**
    * 4. 알람 전체 삭제 테스트 (DELETE /api/v1/alarm/all)
    */
    @Test
    @DisplayName("알람 전체 삭제 성공")
    @WithMockUser(username = "testuser", roles = {"USER"}) // ✅ 인증된 사용자 추가
    void testDeleteAllAlarms_Success() throws Exception {
        Map<String, Object> mockResponse = Map.of("success", true, "deletedCount", 10);

        when(alarmService.deleteAllAlarms(any(Long.class))).thenReturn(mockResponse);

        mockMvc.perform(delete("/api/v1/alarm/all").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.deletedCount").value(10));
    }

}
