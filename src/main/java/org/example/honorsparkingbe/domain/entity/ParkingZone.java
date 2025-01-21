package org.example.honorsparkingbe.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table( name = "parkingZone")


public class ParkingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 위도
    private Float latitude;

    // 경도
    private Float longitude;

    // 주차장 이름
    private String zoneName;

    // City 연관 관계
    @ManyToOne
    @JoinColumn(name = "cityId", nullable = false)
    private City city;

    // District 연관 관계
    @ManyToOne
    @JoinColumn(name = "districtId", nullable = false)
    private District district;

    // EupMyeonDong 연관 관계
    @ManyToOne
    @JoinColumn(name = "eupMyeonDongId", nullable = false)
    private EupMyeonDong eupMyeonDong;

    // 전기차 주차 공간 수
    private Integer electricCarSpaceCount;

    // 최대 주차 가능 수
    private Integer size;

    // 층수 배열 (JSON 형태로 저장)
    @ElementCollection
    @CollectionTable(name = "parkingZoneFloors", joinColumns = @JoinColumn(name = "parkingZoneId"))
    @Column(name = "floor")
    private List<Integer> floor;

    // 최대 요금
    private Integer maxCost;

    // 주소
    private String address;
}
