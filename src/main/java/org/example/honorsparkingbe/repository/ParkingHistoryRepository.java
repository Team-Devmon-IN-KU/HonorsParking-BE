package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ParkingHistoryRepository extends JpaRepository<ParkingHistoryEntity, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE ParkingHistoryEntity p SET p.deleteAt = :deleteAt WHERE p.id IN :ids")
    void updateDeleteAtByIds(@Param("deleteAt") LocalDateTime deleteAt, @Param("ids") List<Long> ids);

    @Query("SELECT p FROM ParkingHistoryEntity p WHERE p.id IN :idList AND p.memberEntity.id = :memberId")
    List<ParkingHistoryEntity> findByIdsAndMember(@Param("idList") List<Long> idList, @Param("memberId") Long memberId);

    void deleteAllByDeleteAtBefore(LocalDateTime deleteAt);
}
