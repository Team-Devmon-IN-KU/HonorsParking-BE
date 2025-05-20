package org.example.honorsparkingbe.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse;
import org.example.honorsparkingbe.dto.response.PaginationResponse;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse.KakaoLocalDocument;
import org.example.honorsparkingbe.util.client.KakaoLocalSearchClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchLocalService {

  static int PAGESIZE = 10;
  private final KakaoLocalSearchClient kakaoLocalSearchClient;

  public SearchLocalResponse getLocalInfoByKeyword(SearchLocalDTO dto) {

    KakaoLocalClientResponse res = kakaoLocalSearchClient.getLoocalsByKeyword(dto);

    // 1. get Meta
    SearchLocalResponse.Meta meta = SearchLocalResponse.Meta.builder()
        .keyword(res.getMeta().getSameName().getKeyword())
        .isEnd(res.getMeta().isEnd())
        .pagination(PaginationResponse.builder()
            .currentPage(dto.getPage() != null ? dto.getPage() : 0)
            .totalPages(res.getMeta().getPageableCount() == 0 ? 0
                : res.getMeta().getTotalCount() / PAGESIZE + 1)
            .pageSize(PAGESIZE)
            .totalItems(res.getMeta().getTotalCount())
            .build())
        .build();

    // 2. get Documents
    List<KakaoLocalDocument> documents = res.getDocuments().stream().map(
        document -> KakaoLocalDocument.builder()
            .placeName(document.getPlaceName())
            .distance(document.getDistance())
            .categoryName(document.getCategoryName())
            .addressName(document.getAddressName())
            .roadAddressName(document.getRoadAddressName())
            .y(Double.parseDouble(document.getY()))
            .x(Double.parseDouble(document.getX()))
            .build()
    ).toList();

    return SearchLocalResponse.builder()
        .meta(meta)
        .documents(documents)
        .build();
  }
}
