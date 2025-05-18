package org.example.honorsparkingbe.unit.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.catalina.security.SecurityConfig;
import org.example.honorsparkingbe.Slf4jRestControllerAdvice;
import org.example.honorsparkingbe.controller.SearchLocalController;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.security.CustomUserDetails;
import org.example.honorsparkingbe.service.SearchLocalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest
@ContextConfiguration(classes = SecurityConfig.class)
public class SearchLocalControllerTest {

  @InjectMocks
  SearchLocalController searchLocalController;
  @Mock
  SearchLocalService searchLocalService;
  @Mock
  private MemberEntity memberEntity;

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
        .standaloneSetup(searchLocalController)
        .setControllerAdvice(new Slf4jRestControllerAdvice()) // 예외 핸들러 등록
        .setCustomArgumentResolvers() // 필요하면 여기에 추가
        .build();
  }

  @Test
  @DisplayName("올바른 요청값 케이스들이 왔을 때 정상 작동하는지 확인")
  void getLoaclDataByKeowrdByRightRequest() throws Exception {
    //Given
    String keyword = "cgv";
    Double longitudeX = 126.9780;
    Double latitudeY = 37.5665;
    MultiValueMap<String, String> onlyKeywordParams = new LinkedMultiValueMap<>();
    onlyKeywordParams.add("keyword", keyword);

    MultiValueMap<String, String> keywordAndPointsParams = new LinkedMultiValueMap<>();
    keywordAndPointsParams.add("keyword", keyword);
    keywordAndPointsParams.add("longitudeX", longitudeX.toString());
    keywordAndPointsParams.add("latitudeY", latitudeY.toString());

    MultiValueMap<String, String> KeywordAndPointsAndPageParams = new LinkedMultiValueMap<>();
    KeywordAndPointsAndPageParams.add("keyword", keyword);
    KeywordAndPointsAndPageParams.add("longitudeX", longitudeX.toString());
    KeywordAndPointsAndPageParams.add("latitudeY", latitudeY.toString());
    KeywordAndPointsAndPageParams.add("page", "2");

    SearchLocalDTO expectedDTO1 = SearchLocalDTO.builder()
        .memberId(100L).keyword(keyword)
        .build();
    SearchLocalDTO expectedDTO2 = SearchLocalDTO.builder()
        .memberId(100L).keyword(keyword).longitudeX(longitudeX).latitudeY(latitudeY)
        .build();
    SearchLocalDTO expectedDTO3 = SearchLocalDTO.builder()
        .memberId(100L).keyword(keyword).longitudeX(longitudeX).latitudeY(latitudeY).page(2)
        .build();
    // when & then
    mockMvc.perform(get("/api/v1/search/local").params(onlyKeywordParams))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/v1/search/local").params(keywordAndPointsParams))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/v1/search/local").params(KeywordAndPointsAndPageParams))
        .andExpect(status().isOk());

    verify(searchLocalService, times(1))
        .getLocalInfoByKeyword(eq(expectedDTO1));
    verify(searchLocalService, times(1))
        .getLocalInfoByKeyword(eq(expectedDTO2));
    verify(searchLocalService, times(1))
        .getLocalInfoByKeyword(eq(expectedDTO3));

  }

  @Test
  @DisplayName("필수 값들이 없는 케이스들이 왔을 때 에러처리가 잘 작동안하는지 확인")
  void getLoaclDataByKeowrdByNonRightRequest() throws Exception {
    // Given
    String keyword = "cgv";
    Double longitudeX = 126.9780;
    Double latitudeY = 37.5665;

    MultiValueMap<String, String> NoneKeywordParams = new LinkedMultiValueMap<>();
    String NonKeywordErrMsg = "keyword 쿼리 파라매터는 필수입니다.";

    MultiValueMap<String, String> NoneLatitudeParams = new LinkedMultiValueMap<>();
    NoneLatitudeParams.add("keyword", keyword);
    NoneLatitudeParams.add("longitudeX", longitudeX.toString());
    String NoneLatitudeErrMsg = "latitude와 longitude는 둘 다 존재하거나 둘 다 없어야 합니다.";

    MultiValueMap<String, String> NoneLongitudeParams = new LinkedMultiValueMap<>();
    NoneLongitudeParams.add("keyword", keyword);
    NoneLongitudeParams.add("latitudeY", latitudeY.toString());
    String NoneLongitudeErrMsg = "latitude와 longitude는 둘 다 존재하거나 둘 다 없어야 합니다.";

    MultiValueMap<String, String> MinusPageParams = new LinkedMultiValueMap<>();
    MinusPageParams.add("keyword", keyword);
    MinusPageParams.add("page", "-111");
    String MinusPageErrMsg = "page는 0 이상이어야 합니다.";

    mockMvc.perform(get("/api/v1/search/local").params(NoneKeywordParams))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").value(NonKeywordErrMsg));

    mockMvc.perform(get("/api/v1/search/local").params(NoneLatitudeParams))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").value(NoneLatitudeErrMsg));

    mockMvc.perform(get("/api/v1/search/local").params(NoneLongitudeParams))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").value(NoneLongitudeErrMsg));

    mockMvc.perform(get("/api/v1/search/local").params(MinusPageParams))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400))
        .andExpect(jsonPath("$.message").value(MinusPageErrMsg));

  }

}
