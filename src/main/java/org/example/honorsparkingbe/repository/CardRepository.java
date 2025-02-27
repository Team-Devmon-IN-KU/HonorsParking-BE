package org.example.honorsparkingbe.repository;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {

  List<CardEntity> findCardEntityByMemberEntity_Id(Long memberId);

}