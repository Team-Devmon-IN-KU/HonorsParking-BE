package org.example.honorsparkingbe.repository;

import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  boolean existsByAuthId(String authId);

  MemberEntity findByAuthId(String authId);
}