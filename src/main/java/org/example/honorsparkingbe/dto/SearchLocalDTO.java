package org.example.honorsparkingbe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchLocalDTO {

  private Long memberId;
  private String keyword;
  private Double longitudeX;
  private Double latitudeY;
  private Integer page;
}
