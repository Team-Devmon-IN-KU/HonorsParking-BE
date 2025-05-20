package org.example.honorsparkingbe.service;

import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.dto.response.KakaoLocalClientResponse;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse;
import org.example.honorsparkingbe.util.client.KakaoLocalSearchClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchLocalService {

  private final KakaoLocalSearchClient kakaoLocalSearchClient;

  public SearchLocalResponse getLocalInfoByKeyword(SearchLocalDTO dto) {

    KakaoLocalClientResponse res = kakaoLocalSearchClient.getLoocalsByKeyword(dto);
    return SearchLocalResponse.builder().build();
  }
}
