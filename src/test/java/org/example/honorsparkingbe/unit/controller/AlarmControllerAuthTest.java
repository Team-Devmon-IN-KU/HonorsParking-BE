package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
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
import org.example.honorsparkingbe.dto.*;
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
        MemberEntity mockMember = MemberEntity.builder()
                .id(1L)
                .authId("testuser")
                .role(MemberRole.ROLE_USER)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(mockMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    /**
     * 1-1. (일반 로그인) 알람 목록 조회 테스트 (GET /api/v1/alarmAll)
     */
    @Test
    @DisplayName("일반 로그인 사용자 - 알람 목록 조회 성공")
    void testGetAlarms_Success() throws Exception {
        // given
        Map<String, Object> mockResponse = Map.of(
                "pagination", Map.of("currentPage", 1),
                "alarms", List.of(Map.of("id", 1L, "content", "Test Alarm"))
        );

        when(alarmService.getAlarms(any(Long.class), any(), any(Integer.class), any(Integer.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/v1/alarmAll")
                        .param("category", "info")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(2)) // 로직상 +1 처리
                .andExpect(jsonPath("$.alarms[0].content").value("Test Alarm"));
    }
//    @WithMockUser(username = "testuser", roles = {"USER"})
//    void testGetAlarms_Success() throws Exception {
//
//        // Given
//        Map<String, Object> mockResponse = Map.of(
//                "pagination", Map.of("currentPage", 1),  // 0페이지가 입력되면 +1을 적용하는 로직
//                "alarms", List.of(Map.of("id", 1L, "content", "Test Alarm"))
//        );
//
//        when(alarmService.getAlarms(any(Long.class), any(), any(Integer.class), any(Integer.class)))
//                .thenReturn(mockResponse);
//
//        // When & Then
//        mockMvc.perform(get("/api/v1/alarmAll")
//                        .param("category", "info")
//                        .param("page", "1")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.pagination.currentPage").value(2))  // 기존 1 → 2로 수정
//                .andExpect(jsonPath("$.alarms[0].content").value("Test Alarm"));
//    }


    /**
     * 1-2. (구글 로그인) 알람 목록 조회 테스트 (GET /api/v1/alarmAll)
     */
    @Test
    @DisplayName("구글 로그인 사용자 - 알람 목록 조회 성공")
    void testGetAlarms_Success_WithSocialLogin() throws Exception {
        // 1. attributes
        Map<String, Object> attributes = Map.of(
                "sub", "google 12345",
                "name", "소셜유저",
                "email", "social@user.com"
        );

        // 2. OAuth2Response mock
        OAuth2Response mockResponse = new GoogleResponse(attributes);

        // 3. CustomOAuth2User
        CustomOAuth2User socialUser = new CustomOAuth2User(mockResponse, "ROLE_USER", 6L);

        // 4. SecurityContextHolder 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                socialUser, null, socialUser.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 5. 서비스 mock 응답
        Map<String, Object> mockResponseData = Map.of(
                "pagination", Map.of("currentPage", 1),
                "alarms", List.of(Map.of("id", 6L, "content", "Social Alarm"))
        );

        when(alarmService.getAlarms(any(Long.class), any(), any(Integer.class), any(Integer.class)))
                .thenReturn(mockResponseData);

        // 6. API 호출 및 검증
        mockMvc.perform(get("/api/v1/alarmAll")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(2))
                .andExpect(jsonPath("$.alarms[0].content").value("Social Alarm"));
    }

    /**
     * 1-3. (카카오 로그인) 알람 목록 조회 테스트 (GET /api/v1/alarmAll)
     */
    @Test
    @DisplayName("카카오 로그인 사용자 - 알람 목록 조회 성공")
    void testGetAlarms_Success_WithKakaoLogin() throws Exception {
        // 1. attributes (카카오 스타일)
        Map<String, Object> attributes = Map.of(
                "id", 98765L,
                "kakao_account", Map.of(
                        "email", "kakao@user.com",
                        "profile", Map.of("nickname", "카카오유저"),
                        "birthday", "0101",
                        "birthyear", "1990",
                        "phone_number", "010-1234-5678"
                )
        );

        // 2. OAuth2Response mock - KakaoResponse
        OAuth2Response kakaoResponse = new KakaoResponse(attributes);

        // 3. CustomOAuth2User 생성
        CustomOAuth2User kakaoUser = new CustomOAuth2User(kakaoResponse, "ROLE_USER", 7L);

        // 4. SecurityContextHolder 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                kakaoUser, null, kakaoUser.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 5. 서비스 mock 응답 설정
        Map<String, Object> mockResponseData = Map.of(
                "pagination", Map.of("currentPage", 0),
                "alarms", List.of(Map.of("id", 7L, "content", "Kakao Alarm"))
        );

        when(alarmService.getAlarms(any(Long.class), any(), any(Integer.class), any(Integer.class)))
                .thenReturn(mockResponseData);

        // 6. API 호출 및 응답 검증
        mockMvc.perform(get("/api/v1/alarmAll")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(1))
                .andExpect(jsonPath("$.alarms[0].content").value("Kakao Alarm"));
    }

    /**
     * 1-4. (네이버 로그인) 알람 목록 조회 테스트 (GET /api/v1/alarmAll)
     */
    @Test
    @DisplayName("네이버 로그인 사용자 - 알람 목록 조회 성공")
    void testGetAlarms_Success_WithNaverLogin() throws Exception {
        // 1. attributes (네이버는 응답 데이터가 nested로 오는 구조)
        Map<String, Object> responseData = Map.of(
                "id", "naver_abc123",
                "name", "네이버유저",
                "email", "naver@user.com",
                "mobile", "010-1234-5678",
                "birthday", "12-31",
                "birthyear", "1994"
        );

        Map<String, Object> attributes = Map.of("response", responseData);

        // 2. NaverResponse mock
        OAuth2Response mockResponse = new NaverResponse(attributes);

        // 3. CustomOAuth2User 생성
        CustomOAuth2User naverUser = new CustomOAuth2User(mockResponse, "ROLE_USER", 8L);

        // 4. SecurityContextHolder 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                naverUser, null, naverUser.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 5. 서비스 mock 응답
        Map<String, Object> mockResponseData = Map.of(
                "pagination", Map.of("currentPage", 1),
                "alarms", List.of(Map.of("id", 8L, "content", "Naver Alarm"))
        );

        when(alarmService.getAlarms(any(Long.class), any(), any(Integer.class), any(Integer.class)))
                .thenReturn(mockResponseData);

        // 6. 호출 및 검증
        mockMvc.perform(get("/api/v1/alarmAll")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagination.currentPage").value(2))
                .andExpect(jsonPath("$.alarms[0].content").value("Naver Alarm"));
    }


    /**
     * 읽지 않은 알람 존재 여부 조회 (GET /api/v1/alarmUnread)
     */
    @Test
    @DisplayName("읽지 않은 알람 존재 여부 조회")
    void testGetUnreadAlarms_Success() throws Exception {
        when(alarmService.hasUnreadAlarms(any(Long.class))).thenReturn(true);

        mockMvc.perform(get("/api/v1/alarmUnread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasUnread").value(true));
    }
//    @WithMockUser(username = "testuser", roles = {"USER"})
//    void testGetUnreadAlarms_Success() throws Exception {
//        when(alarmService.hasUnreadAlarms(any(Long.class))).thenReturn(true);
//
//        mockMvc.perform(get("/api/v1/alarmUnread"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.hasUnread").value(true));
//    }





    /**
 * 4. 알람 전체 삭제 테스트 (DELETE /api/v1/alarm/all)
 */
    @Test
    @DisplayName("알람 전체 삭제 성공")
    void testDeleteAllAlarms_Success() throws Exception {
        Map<String, Object> mockResponse = Map.of("success", true, "deletedCount", 10);

        when(alarmService.deleteAllAlarms(any(Long.class))).thenReturn(mockResponse);

        mockMvc.perform(delete("/api/v1/alarm/all").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.deletedCount").value(10));
    }
//    @WithMockUser(username = "testuser", roles = {"USER"}) // 인증된 사용자 추가
//    void testDeleteAllAlarms_Success() throws Exception {
//        Map<String, Object> mockResponse = Map.of("success", true, "deletedCount", 10);
//
//        when(alarmService.deleteAllAlarms(any(Long.class))).thenReturn(mockResponse);
//
//        mockMvc.perform(delete("/api/v1/alarm/all").with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.deletedCount").value(10));
//    }



}
