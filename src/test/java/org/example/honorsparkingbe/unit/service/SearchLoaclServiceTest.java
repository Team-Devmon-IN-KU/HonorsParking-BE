package org.example.honorsparkingbe.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse.Document;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse.SameName;
import org.example.honorsparkingbe.dto.response.PaginationResponse;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse.KakaoLocalDocument;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse.Meta;
import org.example.honorsparkingbe.service.SearchLocalService;
import org.example.honorsparkingbe.util.client.KakaoLocalSearchClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SearchLoaclServiceTest {

  @InjectMocks
  SearchLocalService searchLocalService;

  @Mock
  KakaoLocalSearchClient kakaoLocalSearchClient;

  @Test
  @DisplayName("키워드와 일치하는 로컬 데이터가 있을 때 page 0 요청 확인")
  void keywordMatchedSuccessTestWithPage0() {
    // Given
    String targetKeyword = "cgv";
    SearchLocalDTO dtoWithPage0 = SearchLocalDTO.builder()
        .keyword(targetKeyword)
        .memberId(1L)
        .build();

    KakaoLocalClientResponse mockClientRes = KakaoLocalClientResponse.builder()
        .meta(
            KakaoLocalClientResponse.Meta.builder()
                .sameName(SameName.builder()
                    .region(List.of()).keyword(targetKeyword).selectedRegion("").build())
                .pageableCount(2)
                .totalCount(2)
                .isEnd(true)
                .build()
        )
        .documents(
            List.of(
                Document.builder()
                    .placeName("cgv 강남점")
                    .distance("123")
                    .placeUrl("")
                    .categoryName("test category")
                    .addressName("서울시 강남동")
                    .roadAddressName("강남로 12")
                    .id("123123")
                    .phone("")
                    .categoryGroupName("test category group")
                    .categoryGroupCode("123")
                    .x("111.111")
                    .y("11.11")
                    .build()
                ,
                Document.builder()
                    .placeName("cgv 논현")
                    .distance("234")
                    .placeUrl("")
                    .categoryName("test category")
                    .addressName("서울시 논현동")
                    .roadAddressName("논현로 12")
                    .id("321321")
                    .phone("")
                    .categoryGroupName("test category group")
                    .categoryGroupCode("321")
                    .x("222.222")
                    .y("22.22")
                    .build()
            )
        )
        .build();

    when(kakaoLocalSearchClient.getLoocalsByKeyword(dtoWithPage0)).thenReturn(mockClientRes);

    SearchLocalResponse expectLocalAPIRes = SearchLocalResponse.builder()
        .meta(Meta.builder().isEnd(true).keyword(targetKeyword)
            .pagination(PaginationResponse.builder()
                .totalItems(2)
                .pageSize(10)
                .totalPages(1)
                .currentPage(0)
                .build())
            .build())
        .documents(List.of(
            KakaoLocalDocument.builder()
                .placeName(mockClientRes.getDocuments().get(0).getPlaceName())
                .distance(mockClientRes.getDocuments().get(0).getDistance())
                .categoryName(mockClientRes.getDocuments().get(0).getCategoryName())
                .addressName(mockClientRes.getDocuments().get(0).getAddressName())
                .roadAddressName(mockClientRes.getDocuments().get(0).getRoadAddressName())
                .y(Double.parseDouble(mockClientRes.getDocuments().get(0).getY()))
                .x(Double.parseDouble(mockClientRes.getDocuments().get(0).getX()))
                .build(),
            KakaoLocalDocument.builder()
                .placeName(mockClientRes.getDocuments().get(1).getPlaceName())
                .distance(mockClientRes.getDocuments().get(1).getDistance())
                .categoryName(mockClientRes.getDocuments().get(1).getCategoryName())
                .addressName(mockClientRes.getDocuments().get(1).getAddressName())
                .roadAddressName(mockClientRes.getDocuments().get(1).getRoadAddressName())
                .y(Double.parseDouble(mockClientRes.getDocuments().get(1).getY()))
                .x(Double.parseDouble(mockClientRes.getDocuments().get(1).getX()))
                .build()))
        .build();

    // When
    SearchLocalResponse resPage = searchLocalService.getLocalInfoByKeyword(dtoWithPage0);

    // Then
    assertEquals(expectLocalAPIRes, resPage);

    verify(kakaoLocalSearchClient, times(1))
        .getLoocalsByKeyword(dtoWithPage0);
  }

  @Test
  @DisplayName("키워드와 일치하는 로컬 데이터가 있을 때 page 1 요청 확인")
  void keywordMatchedSuccessTestWithPage1() {
    // Given
    String targetKeyword = "cgv";
    Double targetX = 127.0;
    Double targetY = 36.5;
    SearchLocalDTO dtoWithPage0 = SearchLocalDTO.builder()
        .keyword(targetKeyword)
        .latitudeY(targetY)
        .longitudeX(targetX)
        .memberId(1L)
        .page(1)
        .build();

    KakaoLocalClientResponse mockClientRes = KakaoLocalClientResponse.builder()
        .meta(
            KakaoLocalClientResponse.Meta.builder()
                .sameName(SameName.builder()
                    .region(List.of()).keyword(targetKeyword).selectedRegion("").build())
                .pageableCount(12)
                .totalCount(12)
                .isEnd(true)
                .build()
        )
        .documents(
            List.of(
                Document.builder()
                    .placeName("cgv 강남점")
                    .distance("123")
                    .placeUrl("")
                    .categoryName("test category")
                    .addressName("서울시 강남동")
                    .roadAddressName("강남로 12")
                    .id("123123")
                    .phone("")
                    .categoryGroupName("test category group")
                    .categoryGroupCode("123")
                    .x("111.111")
                    .y("11.11")
                    .build()
                ,
                Document.builder()
                    .placeName("cgv 논현")
                    .distance("234")
                    .placeUrl("")
                    .categoryName("test category")
                    .addressName("서울시 논현동")
                    .roadAddressName("논현로 12")
                    .id("321321")
                    .phone("")
                    .categoryGroupName("test category group")
                    .categoryGroupCode("321")
                    .x("222.222")
                    .y("22.22")
                    .build()
            )
        )
        .build();

    when(kakaoLocalSearchClient.getLoocalsByKeyword(dtoWithPage0)).thenReturn(mockClientRes);

    SearchLocalResponse expectLocalAPIRes = SearchLocalResponse.builder()
        .meta(Meta.builder().isEnd(true).keyword(targetKeyword)
            .pagination(PaginationResponse.builder()
                .totalItems(12)
                .pageSize(10)
                .totalPages(2)
                .currentPage(1)
                .build())
            .build())
        .documents(List.of(
            KakaoLocalDocument.builder()
                .placeName(mockClientRes.getDocuments().get(0).getPlaceName())
                .distance(mockClientRes.getDocuments().get(0).getDistance())
                .categoryName(mockClientRes.getDocuments().get(0).getCategoryName())
                .addressName(mockClientRes.getDocuments().get(0).getAddressName())
                .roadAddressName(mockClientRes.getDocuments().get(0).getRoadAddressName())
                .y(Double.parseDouble(mockClientRes.getDocuments().get(0).getY()))
                .x(Double.parseDouble(mockClientRes.getDocuments().get(0).getX()))
                .build(),
            KakaoLocalDocument.builder()
                .placeName(mockClientRes.getDocuments().get(1).getPlaceName())
                .distance(mockClientRes.getDocuments().get(1).getDistance())
                .categoryName(mockClientRes.getDocuments().get(1).getCategoryName())
                .addressName(mockClientRes.getDocuments().get(1).getAddressName())
                .roadAddressName(mockClientRes.getDocuments().get(1).getRoadAddressName())
                .y(Double.parseDouble(mockClientRes.getDocuments().get(1).getY()))
                .x(Double.parseDouble(mockClientRes.getDocuments().get(1).getX()))
                .build()))
        .build();

    // When
    SearchLocalResponse resPage = searchLocalService.getLocalInfoByKeyword(dtoWithPage0);

    // Then
    assertEquals(expectLocalAPIRes, resPage);

    verify(kakaoLocalSearchClient, times(1))
        .getLoocalsByKeyword(dtoWithPage0);
  }

  @Test
  @DisplayName("키워드와 일치하는 로컬 데이터가 없을 때 확인")
  void keywordMatchedFailTest() {
    // Given
    String targetKeyword = "cgv";
    Double targetX = 127.0;
    Double targetY = 36.5;
    SearchLocalDTO dto = SearchLocalDTO.builder()
        .keyword(targetKeyword)
        .latitudeY(targetY)
        .longitudeX(targetX)
        .memberId(1L)
        .page(0)
        .build();

    KakaoLocalClientResponse mockClientRes = KakaoLocalClientResponse.builder()
        .meta(
            KakaoLocalClientResponse.Meta.builder()
                .sameName(SameName.builder()
                    .region(List.of()).keyword(targetKeyword).selectedRegion("").build())
                .pageableCount(0)
                .totalCount(0)
                .isEnd(true)
                .build()
        )
        .documents(List.of())
        .build();

    when(kakaoLocalSearchClient.getLoocalsByKeyword(dto)).thenReturn(mockClientRes);

    SearchLocalResponse expectLocalAPIRes = SearchLocalResponse.builder()
        .meta(Meta.builder().isEnd(true).keyword(targetKeyword)
            .pagination(PaginationResponse.builder()
                .totalItems(0)
                .pageSize(10)
                .totalPages(0)
                .currentPage(0)
                .build())
            .build())
        .documents(List.of())
        .build();

    // When
    SearchLocalResponse resPage = searchLocalService.getLocalInfoByKeyword(dto);

    // Then
    assertEquals(expectLocalAPIRes, resPage);

    verify(kakaoLocalSearchClient, times(1))
        .getLoocalsByKeyword(dto);
  }

  @Test
  @DisplayName("카카오 클라이언트에서 에러가 났을 때 예외처리 확인")
  void whenKakaoClientFailTest() {

    // Given
    String targetKeyword = "cgv";
    Double targetX = 127.0;
    Double targetY = 36.5;
    SearchLocalDTO dto = SearchLocalDTO.builder()
        .keyword(targetKeyword)
        .latitudeY(targetY)
        .longitudeX(targetX)
        .memberId(1L)
        .page(0)
        .build();

    // 클라이언트 호출 시 500 에러 시뮬레이션 - RuntimeException 던짐
    when(kakaoLocalSearchClient.getLoocalsByKeyword(dto))
        .thenThrow(new RuntimeException("Internal Server Error"));

    // When & Then: 예외가 발생하는지 검증
    RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
      searchLocalService.getLocalInfoByKeyword(dto);
    });

    assertEquals("Internal Server Error", thrown.getMessage());

    verify(kakaoLocalSearchClient, times(1)).getLoocalsByKeyword(dto);
  }
}
