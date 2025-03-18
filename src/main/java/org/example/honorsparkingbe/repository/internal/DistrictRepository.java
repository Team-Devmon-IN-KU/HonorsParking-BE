package org.example.honorsparkingbe.repository.internal;

import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistrictRepository extends JpaRepository<DistrictEntity, Long> {

  Optional<DistrictEntity> findByName(String Name);
}
