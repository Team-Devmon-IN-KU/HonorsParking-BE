package org.example.honorsparkingbe.repository.internal;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, Long> {

  List<CarEntity> findAllByCarNumberIn(List<String> carNumbers);
}
