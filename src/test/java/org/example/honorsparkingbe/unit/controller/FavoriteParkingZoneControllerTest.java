package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.example.honorsparkingbe.controller.FavoriteParkingZoneController;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.ToggleFavoriteParkingZoneDTO;
import org.example.honorsparkingbe.dto.request.ToggleFavoriteParkingZoneRequest;
import org.example.honorsparkingbe.dto.response.ToggleFavoriteParkingZoneResponse;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.FavoriteParkingZoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
@ContextConfiguration(classes = SecurityConfig.class)
public class FavoriteParkingZoneControllerTest {

  @Mock
  private MemberEntity memberEntity;
  @Mock
  FavoriteParkingZoneService favoriteParkingZoneService;
  @InjectMocks
  FavoriteParkingZoneController favoriteParkingZoneController;

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
        .standaloneSetup(favoriteParkingZoneController)
        .setCustomArgumentResolvers()
        .build();
  }

  @Test
  @DisplayName("favoriteParkingZoneController-toggleFavoriteParkingzone테스트 올바른값이 리턴")
  void postFavoriteParkingZone_ShouldReturenRightValue() throws Exception {

    //Given
    //1. sample request
    ToggleFavoriteParkingZoneRequest request = ToggleFavoriteParkingZoneRequest.builder()
        .parkingZoneId(999L)
        .isBookmark(true)
        .build();
    String content = objectMapper.writeValueAsString(request);
    //2. Mock service 설정
    when(favoriteParkingZoneService.toggleFavoriteParkingZone(
        any(ToggleFavoriteParkingZoneDTO.class)))
        .thenReturn(any(ToggleFavoriteParkingZoneResponse.class));

    // When
    mockMvc.perform(post("/api/v1/parking/parkingzone/bookmark").with(csrf()).contentType(
            MediaType.APPLICATION_JSON).content(content))
        .andExpect(status().isOk());

    verify(favoriteParkingZoneService, times(1)).toggleFavoriteParkingZone(any());
  }
}
