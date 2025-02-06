package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingHistoryRepository extends JpaRepository<ParkingHistoryEntity, Long> {

    @Override
    void deleteAllByIdInBatch(Iterable<Long> longs); //Batch를 통한 속도 향상

    @Query("SELECT p FROM ParkingHistoryEntity p WHERE p.id IN :idList AND p.memberEntity.id = :memberId")
    List<ParkingHistoryEntity> findByIdsAndMember(@Param("idList") List<Long> idList, @Param("memberId") Long memberId);
}
