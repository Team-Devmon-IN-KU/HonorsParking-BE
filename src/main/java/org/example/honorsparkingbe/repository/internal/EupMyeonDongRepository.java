package org.example.honorsparkingbe.repository.internal;

import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EupMyeonDongRepository extends JpaRepository<EupMyeonDongEntity, Long> {

  Optional<EupMyeonDongEntity> findByName(String Name);
}
