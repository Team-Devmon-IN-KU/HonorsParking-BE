package org.example.honorsparkingbe.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
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
        .latitudeY(36.5)
        .longitudeX(127.0)
        .memberId(1L)
        .page(0)
        .build();
    SearchLocalDTO dtoWithPage1 = SearchLocalDTO.builder()
        .keyword(targetKeyword)
        .latitudeY(36.5)
        .longitudeX(127.0)
        .memberId(1L)
        .page(0)
        .build();

    SearchLocalResponse mockKakaoLocalAPIRes = SearchLocalResponse.builder()
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
                .placeName("cgv 용산점")
                .distance("123")
                .categoryName("test category")
                .addressName("test address")
                .roadAddressName("test road address")
                .y(35.5)
                .x(127.3)
                .build(),
            KakaoLocalDocument.builder()
                .placeName("cgv 이태원점")
                .distance("123")
                .categoryName("test category")
                .addressName("test address")
                .roadAddressName("test road address")
                .y(35.5)
                .x(127.3)
                .build()))
        .build();

    // When
    SearchLocalResponse resPage = searchLocalService.getLocalInfoByKeyword(dtoWithPage0);

    // Then
    assertEquals(targetKeyword, resPage.getMeta().getKeyword());
    assertEquals(true, resPage.getMeta().getIsEnd());
    assertEquals(10, resPage.getMeta().getPagination().getPageSize());
    assertEquals(0, resPage.getMeta().getPagination().getCurrentPage());
    assertEquals(1, resPage.getMeta().getPagination().getTotalPages());
    assertEquals(2, resPage.getMeta().getPagination().getTotalItems());
    assertEquals(2, resPage.getDocuments().size());
    assertEquals(resPage.getDocuments().get(0), mockKakaoLocalAPIRes.getDocuments().get(0));
    assertEquals(resPage.getDocuments().get(1), mockKakaoLocalAPIRes.getDocuments().get(1));
  }

  @Test
  @DisplayName("키워드와 일치하는 로컬 데이터가 있을 때 page 1 요청 확인")
  void keywordMatchedSuccessTestWithPage1() {
  }

  @Test
  @DisplayName("키워드와 일치하는 로컬 데이터가 없을 때 확인")
  void keywordMatchedFailTest() {

  }

  @Test
  @DisplayName("카카오 클라이언트에서 에러가 났을 때 예외처리 확인")
  void whenKakaoClientFailTest() {

  }
}
