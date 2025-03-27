package org.example.honorsparkingbe.repository.internal;

import org.example.honorsparkingbe.domain.entity.PayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayRepository extends JpaRepository<PayEntity, Long> {

}
