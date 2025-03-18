package org.example.honorsparkingbe.repository.internal;

import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Long> {

  Optional<CityEntity> findByName(String name);
}
