package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<CarEntity, Long> {
    Optional<CarEntity> findByCarNumber(String carNumber);
}
