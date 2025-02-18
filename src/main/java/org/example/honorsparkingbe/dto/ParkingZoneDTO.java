package org.example.honorsparkingbe.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingZoneDTO {

  private Boolean isFavorite;
  private Double latitude;
  private Double longitude;
  private String zoneName;
  private String cityName;
  private String districtName;
  private String eupMyeonDongName;
  private Integer electricCarSpaceCount;
  private Boolean isReservedOk;
  private Integer size;
  private List<Integer> floor;
  private Integer maxCost;
  private List<ParkingFeeRuleDTO> parkingFeeRules;  // 요금 정보
  private String thumbnail;
}
