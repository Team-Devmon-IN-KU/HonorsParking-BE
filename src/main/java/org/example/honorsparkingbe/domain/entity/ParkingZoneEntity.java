package org.example.honorsparkingbe.domain.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.honorsparkingbe.util.converter.persistence.JsonIntegerListConverter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parkingZone")
@Builder

public class ParkingZoneEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 위도
  private Double latitude;

  // 경도
  private Double longitude;

  // 주차장 이름
  private String zoneName;

  // City 연관 관계
  @ManyToOne
  @JoinColumn(name = "cityId", nullable = false)
  private CityEntity cityEntity;

  // District 연관 관계
  @ManyToOne
  @JoinColumn(name = "districtId", nullable = false)
  private DistrictEntity districtEntity;

  // EupMyeonDong 연관 관계
  @ManyToOne
  @JoinColumn(name = "eupMyeonDongId", nullable = false)
  private EupMyeonDongEntity eupMyeonDongEntity;

  // 전기차 주차 공간 수
  private Integer electricCarSpaceCount;

  // 최대 주차 가능 수
  private Integer size;

  // 층수 배열 Legacy
//  @ElementCollection
//  @CollectionTable(name = "parkingZoneFloors", joinColumns = @JoinColumn(name = "parkingZoneId"))
//  @Column(name = "floor")
//  private List<Integer> floor;

  @Convert(converter = JsonIntegerListConverter.class)
  @Column(name = "floors", columnDefinition = "TEXT")  // MySQL: TEXT, PostgreSQL: JSONB 고려 가능
  private List<Integer> floors;

  // 최대 요금
  private Integer maxCost;

  // 주소
  private String address;

  //해당 주차장 요금
  @OneToMany(mappedBy = "parkingZoneEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ParkingFeeRuleEntity> parkingFeeRuleEntities;
}
