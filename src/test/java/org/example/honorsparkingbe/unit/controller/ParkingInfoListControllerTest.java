package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.example.honorsparkingbe.controller.ParkingZoneInfoController;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.ParkingZoneListDTO;
import org.example.honorsparkingbe.dto.request.ParkingZoneListRequest;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.ParkingZoneInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class ParkingInfoListControllerTest {

  @Mock
  private MemberEntity memberEntity;
  @Mock
  ParkingZoneInfoService parkingZoneInfoService;

  @InjectMocks
  ParkingZoneInfoController parkingZoneInfoController;

  private MockMvc mockMvc;

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
        .standaloneSetup(parkingZoneInfoController)
        .setCustomArgumentResolvers()
        .build();
  }

  @Test
  @DisplayName("올바른 요청값에 대한 컨트롤러 테스트")
  void getParkingZoneInfo_ShouldReturnParkingZoneInfo() throws Exception {

    // Given
    // 1. sample request
    ParkingZoneListRequest request = ParkingZoneListRequest.builder()
        .latitude(36.5)
        .longitude(127.7)
        .build();
    // Mock Service 설정
    when(parkingZoneInfoService.getParkingZones(ParkingZoneListDTO.builder()
        .parkingZoneListRequest(request)
        .userId(1L)
        .build())).thenReturn(any());

    //when
    mockMvc.perform(get("/api/v1/parkingzone/list")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("latitude", String.valueOf(request.getLatitude()))
            .param("longitude", String.valueOf(request.getLongitude())))
        .andExpect(status().isOk());

    verify(parkingZoneInfoService, times(1)).getParkingZones(any());
  }
}
