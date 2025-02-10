package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.example.honorsparkingbe.controller.ParkingHistoryController;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.DeleteParkingHistoryDTO;
import org.example.honorsparkingbe.dto.request.ParkingHistoryDeleteRequest;
import org.example.honorsparkingbe.dto.response.ParkingHistoryDeleteResponse;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.ParkingHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest
@Import({Slf4jRestControllerAdvice.class})  // 예외 처리 적용
@ContextConfiguration(classes = SecurityConfig.class)  // SecurityConfig 추가
public class ParkingHistoryControllerTest {

  @Mock
  private MemberEntity memberEntity;
  @Mock
  private ParkingHistoryService parkingHistoryService;  // 서비스 Mock 객체
  @InjectMocks  // @InjectMocks로 컨트롤러에 Mock 객체 주입
  private ParkingHistoryController parkingHistoryController;

  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
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
        .standaloneSetup(parkingHistoryController)
        .setCustomArgumentResolvers()
        .build();
  }

  @Test
  void getParkingZoneList_ShouldReturnParkingZones() throws Exception {

    // Given
    // 1. sample request
    ParkingHistoryDeleteRequest request = ParkingHistoryDeleteRequest.builder()
        .historyIDList(List.of(1L, 2L))
        .build();
    String content = objectMapper.writeValueAsString(request);

    // Mock service 설정
    when(parkingHistoryService.softDeleteParkingHistories(
        any(DeleteParkingHistoryDTO.class))).thenReturn(any(
        ParkingHistoryDeleteResponse.class));

    // When
    mockMvc.perform(delete("/api/v1/parking/history").with(csrf())
            .contentType(MediaType.APPLICATION_JSON).content(content))
        .andExpect(status().isOk());

    verify(parkingHistoryService, times(1)).softDeleteParkingHistories(any());
  }
}