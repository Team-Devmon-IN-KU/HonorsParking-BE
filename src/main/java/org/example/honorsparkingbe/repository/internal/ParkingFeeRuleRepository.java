package org.example.honorsparkingbe.repository.internal;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.ParkingFeeRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingFeeRuleRepository extends JpaRepository<ParkingFeeRuleEntity, Long> {

  List<ParkingFeeRuleEntity> findAllByParkingZoneEntity_Id(Long parkingZoneId);

  List<ParkingFeeRuleEntity> findByParkingZoneEntityId(Long parkingZoneId);


  @Query("SELECT pfr FROM ParkingFeeRuleEntity pfr WHERE pfr.parkingZoneEntity.id IN :ids")
  List<ParkingFeeRuleEntity> findAllByParkingZoneEntityIdIn(@Param("ids") List<Long> ids);
}
