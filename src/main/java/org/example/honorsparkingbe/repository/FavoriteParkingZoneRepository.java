package org.example.honorsparkingbe.repository;

import java.util.Optional;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteParkingZoneRepository extends
    JpaRepository<FavoriteParkingZoneEntity, Long> {

  @Query("""
          SELECT f 
          FROM FavoriteParkingZoneEntity f 
          WHERE f.memberEntity.id = :memberId 
          ORDER BY f.id ASC
      """)
  Page<FavoriteParkingZoneEntity> findAllByMemberEntity_IdOrderByIdAsc(
      @Param("memberId") Long memberId, Pageable pageable);

  Optional<FavoriteParkingZoneEntity> findByMemberEntity_IdAndParkingZoneEntity_Id(Long memberId,
      Long parkingZoneId);
}
