package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.dto.SearchLocalDTO;
import org.example.honorsparkingbe.dto.response.SearchLocalResponse;
import org.springframework.stereotype.Service;

@Service
public class SearchLocalService {

  public SearchLocalResponse getLocalInfoByKeyword(SearchLocalDTO dto) {

    return SearchLocalResponse.builder().build();
  }
}
