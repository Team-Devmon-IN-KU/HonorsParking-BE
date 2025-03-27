package org.example.honorsparkingbe.repository.internal;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParkingZoneRepository extends JpaRepository<ParkingZoneEntity, Long> {

  boolean existsByZoneName(String name);

  // ID 배열로 주차장 리스트 반환
  @EntityGraph(attributePaths = {"cityEntity", "districtEntity", "eupMyeonDongEntity",
      "parkingFeeRuleEntities"})
  List<ParkingZoneEntity> findAllByIdIn(List<Long> ids);

  // 위경도 기준으로 가까운 거리순으로 정렬된 주차장 리스트 반환
  @Query(value = """
          SELECT p.*,
                 (6371 * acos(
                     cos(radians(:latitude)) * cos(radians(p.latitude)) *
                     cos(radians(p.longitude) - radians(:longitude)) +
                     sin(radians(:latitude)) * sin(radians(p.latitude))
                 )) AS distance
          FROM parkingZone p
          ORDER BY distance ASC
          LIMIT :limit OFFSET :offset
      """, nativeQuery = true)
  List<ParkingZoneEntity> findClosestParkingZones(
      @Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("limit") int limit,
      @Param("offset") int offset
  );

  @Query(value = """
          SELECT p.id,
                 (6371 * acos(
                     cos(radians(:latitude)) * cos(radians(p.latitude)) *
                     cos(radians(p.longitude) - radians(:longitude)) +
                     sin(radians(:latitude)) * sin(radians(p.latitude))
                 )) AS distance
          FROM parkingZone p
          WHERE p.id NOT IN :excludeIds  -- 제외할 parkingZone id를 지정
          ORDER BY distance ASC
          LIMIT :limit OFFSET :offset
      """, nativeQuery = true)
  List<Long> findClosestParkingZonesIDWithExclusion(
      @Param("latitude") double latitude,
      @Param("longitude") double longitude,
      @Param("limit") int limit,
      @Param("offset") int offset,
      @Param("excludeIds") List<Long> excludeIds);

}

